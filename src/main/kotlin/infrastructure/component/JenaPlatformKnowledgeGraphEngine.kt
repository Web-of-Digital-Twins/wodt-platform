/*
 * Copyright (c) 2023. Andrea Giulianelli
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package infrastructure.component

import application.component.EcosystemRegistryMapper
import application.component.PlatformKnowledgeGraphEngine
import application.event.DTEcosystemKGEvent
import application.event.DtkgEvent
import com.apicatalog.jsonld.JsonLdOptions
import com.apicatalog.jsonld.loader.HttpLoader
import com.apicatalog.jsonld.loader.LRUDocumentCache
import entity.digitaltwin.DigitalTwinDescription
import entity.digitaltwin.DigitalTwinDescriptionImplementationType
import entity.digitaltwin.DigitalTwinURI
import entity.ontology.WoDTVocabulary
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.apache.jena.query.QueryExecutionFactory
import org.apache.jena.query.QueryFactory
import org.apache.jena.query.QueryParseException
import org.apache.jena.query.ResultSetFormatter
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.Resource
import org.apache.jena.reasoner.ReasonerRegistry
import org.apache.jena.riot.Lang
import org.apache.jena.riot.RDFParser
import org.apache.jena.riot.RDFWriter
import org.apache.jena.riot.lang.LangJSONLD11.JSONLD_OPTIONS
import org.apache.jena.shared.Lock
import org.apache.jena.sparql.util.Context
import java.io.ByteArrayOutputStream
import java.util.Collections
import java.util.concurrent.Executors

private sealed interface PlatformKGModelEvent

private data class NewDtkgModel(
    val dtMessageCounter: Int,
    val dtUri: DigitalTwinURI,
    val dtkg: Model,
) : PlatformKGModelEvent

private data class NewDtdModel(val dtUri: DigitalTwinURI, val dtd: Model) : PlatformKGModelEvent

/**
 * This class provides an implementation of the [PlatformKnowledgeGraphEngine] component.
 */
class JenaPlatformKnowledgeGraphEngine(
    private val ecosystemRegistryMapper: EcosystemRegistryMapper,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : PlatformKnowledgeGraphEngine {
    // TODO Configure backpressure?
    // private val _platformKnowledgeGraphs = MutableSharedFlow<DTEcosystemKGEvent>(extraBufferCapacity = 1,
    // onBufferOverflow = BufferOverflow.DROP_OLDEST);
    private val _platformKnowledgeGraphs = MutableSharedFlow<DTEcosystemKGEvent>()
    private val engineFlow = MutableSharedFlow<PlatformKGModelEvent>()
    private val engineDispatcher = Executors.newSingleThreadExecutor {
            r ->
        Thread(r, ENGINE_THREAD)
    }.asCoroutineDispatcher()

    private val dtkgsModelMap: MutableMap<DigitalTwinURI, Model> = Collections.synchronizedMap(mutableMapOf())
    private val dtdsModelMap: MutableMap<DigitalTwinURI, Model> = Collections.synchronizedMap(mutableMapOf())

    private val dtkgsModel = ModelFactory.createDefaultModel()
    private val dtdsModel = ModelFactory.createDefaultModel()

    private val platformKnowledgeGraphModel: Model
        get() {
            dtkgsModel.enterCriticalSection(Lock.READ)
            dtdsModel.enterCriticalSection(Lock.READ)
            val platformKGModel = dtkgsModel.union(dtdsModel)
            dtdsModel.leaveCriticalSection()
            dtkgsModel.leaveCriticalSection()
            return platformKGModel
        }

    private val cacheLoader = LRUDocumentCache(HttpLoader.defaultInstance(), CACHE_SIZE)

    private var _dtkgUpdatesMap: Map<DigitalTwinURI, MutableSharedFlow<DtkgEvent>> = mapOf()

    override val platformKnowledgeGraphs: Flow<DTEcosystemKGEvent> = this._platformKnowledgeGraphs.asSharedFlow()

    override fun currentPlatformKnowledgeGraph(): String? = this.platformKnowledgeGraphModel.toTurtle()

    override fun currentCachedDigitalTwinKnowledgeGraph(dtUri: DigitalTwinURI): String? =
        this.dtkgsModelMap[dtUri]?.toTurtle()

    override fun currentCachedDigitalTwinKnowledgeGraphUpdates(dtUri: DigitalTwinURI): Flow<DtkgEvent>? =
        this._dtkgUpdatesMap[dtUri]?.asSharedFlow()

    override fun query(query: String, responseContentType: String?): String? {
        val inferenceModel = ModelFactory.createInfModel(ReasonerRegistry.getOWLReasoner(), platformKnowledgeGraphModel)
        val parsedQuery = try {
            QueryFactory.create(query)
        } catch (e: QueryParseException) {
            return null
        }
        val queryExecution = QueryExecutionFactory.create(parsedQuery, inferenceModel)
        return if (parsedQuery.isSelectType) {
            val resultSet = queryExecution.execSelect()
            val outputStream = ByteArrayOutputStream()

            when (responseContentType) {
                CONTENT_TYPE_CSV -> ResultSetFormatter.outputAsCSV(outputStream, resultSet)
                CONTENT_TYPE_TSV -> ResultSetFormatter.outputAsTSV(outputStream, resultSet)
                CONTENT_TYPE_SPARQL_XML -> ResultSetFormatter.outputAsXML(outputStream, resultSet)
                else -> ResultSetFormatter.outputAsJSON(outputStream, resultSet)
            }
            outputStream.toString()
        } else if (parsedQuery.isAskType) {
            val booleanResult = queryExecution.execAsk()
            val outputStream = ByteArrayOutputStream()

            if (responseContentType == CONTENT_TYPE_SPARQL_XML) {
                ResultSetFormatter.outputAsXML(outputStream, booleanResult)
            } else {
                ResultSetFormatter.outputAsJSON(outputStream, booleanResult)
            }
            outputStream.toString()
        } else if (parsedQuery.isConstructType || parsedQuery.isDescribeType) {
            if (parsedQuery.isConstructType) {
                queryExecution.execConstruct()
            } else {
                queryExecution.execDescribe()
            }.toTurtle()
        } else {
            null
        }
    }

    override fun getDigitalTwinsFromPhysicalAsset(physicalAssetId: String): Set<DigitalTwinURI> {
        val query = """
            SELECT ?dtUri
            WHERE
                { ?dtUri <${WoDTVocabulary.PHYSICAL_ASSET_ID}> "$physicalAssetId" }
        """.trimIndent()
        return this.query(query, CONTENT_TYPE_SPARQL_JSON)?.let { queryResult ->
            val jsonQueryResult = Json.decodeFromString<JsonObject>(queryResult)
            jsonQueryResult["results"]?.jsonObject?.get("bindings")?.jsonArray?.mapNotNull { singleResult ->
                singleResult.jsonObject["dtUri"]
                    ?.jsonObject
                    ?.get("value")
                    ?.jsonPrimitive
                    ?.content
                    ?.let { ecosystemRegistryMapper.getDigitalTwinUri(it) }
            }?.toSet()
        }.orEmpty()
    }

    override fun mergeDigitalTwinDescription(dtd: DigitalTwinDescription) {
        val options = JsonLdOptions()
        options.documentLoader = cacheLoader
        if (dtd.implementationType == DigitalTwinDescriptionImplementationType.THING_DESCRIPTION) {
            val dtdModel = RDFParser.fromString(dtd.obtainRepresentation(), Lang.JSONLD11)
                .context(Context.create().set(JSONLD_OPTIONS, options))
                .build()
                .toModel()
                .mapLocalDigitalTwinModel()
            CoroutineScope(engineDispatcher).launch { engineFlow.emit(NewDtdModel(dtd.digitalTwinUri, dtdModel)) }
        }
    }

    override fun updateDigitalTwinKnowledgeGraph(dtkgEvent: DtkgEvent) {
        val dtkgModel = RDFParser.fromString(dtkgEvent.dtkgPayload, Lang.TTL)
            .build()
            .toModel()
            .mapLocalDigitalTwinModel()
        emitDTKGEvent(dtkgEvent, dtkgModel)
        CoroutineScope(engineDispatcher).launch {
            engineFlow.emit(NewDtkgModel(dtkgEvent.dtMessageCounter, dtkgEvent.dtUri, dtkgModel))
        }
    }

    override suspend fun start() {
        withContext(engineDispatcher) {
            engineFlow.collect { event ->
                when (event) {
                    is NewDtkgModel -> applyDigitalTwinKnowledgeGraphUpdate(event)
                    is NewDtdModel -> addDigitalTwinDescription(event)
                }
            }
        }
    }

    override fun deleteDigitalTwin(digitalTwinUri: DigitalTwinURI): Boolean =
        if (dtkgsModelMap.containsKey(digitalTwinUri)) {
            this.dtkgsModel.enterCriticalSection(Lock.WRITE)
            this.dtkgsModelMap[digitalTwinUri]?.also { this.dtkgsModel.remove(it) }
            this.dtkgsModelMap -= digitalTwinUri
            this.dtkgsModel.leaveCriticalSection()

            this.dtdsModel.enterCriticalSection(Lock.WRITE)
            this.dtdsModelMap[digitalTwinUri]?.also { this.dtdsModel.remove(it) }
            this.dtdsModelMap -= digitalTwinUri
            this.dtdsModel.leaveCriticalSection()

            this.emitPlatformKGEvent(digitalTwinUri)
            true
        } else {
            false
        }

    private fun applyDigitalTwinKnowledgeGraphUpdate(dtkgModelUpdate: NewDtkgModel) {
        this.dtkgsModel.enterCriticalSection(Lock.WRITE)
        this.dtkgsModelMap[dtkgModelUpdate.dtUri]?.also { this.dtkgsModel.remove(it) }
        this.dtkgsModelMap += (dtkgModelUpdate.dtUri to dtkgModelUpdate.dtkg)
        this.dtkgsModel.add(dtkgModelUpdate.dtkg)
        this.emitPlatformKGEvent(dtkgModelUpdate.dtUri, dtkgModelUpdate.dtMessageCounter)
        this.dtkgsModel.leaveCriticalSection()
    }

    private fun addDigitalTwinDescription(dtdModelUpdate: NewDtdModel) {
        var newDT = true
        this.dtdsModel.enterCriticalSection(Lock.WRITE)
        this.dtdsModelMap[dtdModelUpdate.dtUri]?.also {
            this.dtdsModel.remove(it)
            newDT = false
        }
        this.dtdsModelMap += (dtdModelUpdate.dtUri to dtdModelUpdate.dtd)
        this.dtdsModel.add(dtdModelUpdate.dtd)
        this.dtdsModel.leaveCriticalSection()
        if (newDT) { this.handleNewDT(dtdModelUpdate.dtUri) }
        this.emitPlatformKGEvent(dtdModelUpdate.dtUri)
    }

    private fun Model.toTurtle() = RDFWriter.create().lang(Lang.TTL).source(this).asString()

    // TODO serializing the model to Turtle is the main performance bottleneck
    private fun emitPlatformKGEvent(
        originDtUri: DigitalTwinURI,
        originDTMessageCounter: Int? = null,
    ) {
        CoroutineScope(dispatcher).launch {
            _platformKnowledgeGraphs.emit(
                DTEcosystemKGEvent(
                    originDTMessageCounter,
                    originDtUri,
                    platformKnowledgeGraphModel.toTurtle(),
                ),
            )
        }
    }

    private fun emitDTKGEvent(originEvent: DtkgEvent, dtkgModel: Model) {
        CoroutineScope(dispatcher).launch {
            _dtkgUpdatesMap[originEvent.dtUri]?.emit(
                DtkgEvent(originEvent.dtMessageCounter, originEvent.dtUri, dtkgModel.toTurtle()),
            )
        }
    }

    private fun handleNewDT(digitalTwinUri: DigitalTwinURI) {
        this.updateLocalURIsInPlatformKG()
        this._dtkgUpdatesMap += (digitalTwinUri to MutableSharedFlow())
    }

    private fun Model.mapLocalDigitalTwinModel(): Model {
        val mappedModel = ModelFactory.createDefaultModel()
        this.listStatements().forEach { statement ->
            run {
                val tripleSubject = statement.subject
                val tripleObject = statement.getObject()
                val mappedSubject = if (tripleSubject.isURIResource) {
                    ecosystemRegistryMapper.getLocalUrl(DigitalTwinURI((tripleSubject as Resource).uri))?.let {
                        mappedModel.createResource(it)
                    } ?: tripleSubject
                } else { tripleSubject }
                val mappedObject = if (tripleObject.isURIResource) {
                    ecosystemRegistryMapper.getLocalUrl(DigitalTwinURI((tripleObject as Resource).uri))?.let {
                        mappedModel.createResource(it)
                    } ?: tripleObject
                } else { tripleObject }

                mappedModel.add(mappedSubject, statement.predicate, mappedObject)
            }
        }
        return mappedModel
    }

    private fun updateLocalURIsInPlatformKG() {
        this.dtkgsModel.enterCriticalSection(Lock.WRITE)
        this.dtkgsModelMap.entries.filter { cachedDTEntry ->
            cachedDTEntry.value.listStatements().toList()
                .filter { it.getObject().isURIResource }
                .map { (it.getObject() as Resource).uri }
                .any { ecosystemRegistryMapper.getLocalUrl(DigitalTwinURI(it)) != null }
        }.forEach { cachedDTEntry ->
            this.dtkgsModel.remove(cachedDTEntry.value)
            val updatedModel = cachedDTEntry.value.mapLocalDigitalTwinModel()
            this.dtkgsModelMap += (cachedDTEntry.key to updatedModel)
            this.dtkgsModel.add(updatedModel)
        }
        this.dtkgsModel.leaveCriticalSection()
    }

    companion object {
        private const val CONTENT_TYPE_CSV = "text/csv"
        private const val CONTENT_TYPE_TSV = "text/tab-separated-values"
        private const val CONTENT_TYPE_SPARQL_XML = "application/sparql-results+xml"
        private const val CONTENT_TYPE_SPARQL_JSON = "application/sparql-results+json"
        private const val CACHE_SIZE = 50
        private const val ENGINE_THREAD = "KG-Engine-Thread"
    }
}
