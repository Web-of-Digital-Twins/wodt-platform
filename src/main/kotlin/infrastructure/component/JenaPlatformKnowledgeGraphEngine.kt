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
import entity.digitaltwin.DigitalTwinDescriptor
import entity.digitaltwin.DigitalTwinDescriptorImplementationType
import entity.digitaltwin.DigitalTwinURI
import entity.ontology.WoDTVocabulary
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
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
import org.apache.jena.shared.Lock
import java.io.ByteArrayOutputStream

/**
 * This class provides an implementation of the [PlatformKnowledgeGraphEngine] component.
 */
class JenaPlatformKnowledgeGraphEngine(
    private val ecosystemRegistryMapper: EcosystemRegistryMapper,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : PlatformKnowledgeGraphEngine {
    private val _platformKnowledgeGraphs = MutableSharedFlow<String>()

    private var dtkgsModelMap: Map<DigitalTwinURI, Model> = mapOf()
    private var dtdsModelMap: Map<DigitalTwinURI, Model> = mapOf()

    private val dtkgsModel = ModelFactory.createDefaultModel()
    private val dtdsModel = ModelFactory.createDefaultModel()

    private val platformKnowledgeGraphModel: Model
        get() = dtkgsModel.union(dtdsModel)

    override val platformKnowledgeGraphs: Flow<String> = this._platformKnowledgeGraphs.asSharedFlow()

    override fun currentPlatformKnowledgeGraph(): String? = this.platformKnowledgeGraphModel.toTurtle()

    override fun currentCachedDigitalTwinKnowledgeGraph(dtUri: DigitalTwinURI): String? =
        this.dtkgsModelMap[dtUri]?.toTurtle()

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

    override fun mergeDigitalTwinDescriptor(dtd: DigitalTwinDescriptor) {
        if (dtd.implementationType == DigitalTwinDescriptorImplementationType.THING_DESCRIPTION) {
            this.dtdsModel.enterCriticalSection(Lock.WRITE)
            val dtdModel = RDFParser.fromString(dtd.obtainRepresentation(), Lang.JSONLD11)
                .build()
                .toModel()
                .mapLocalDigitalTwinModel()
            this.dtdsModelMap[dtd.digitalTwinUri]?.also { this.dtdsModel.remove(it) }
            this.dtdsModelMap += (dtd.digitalTwinUri to dtdModel)
            this.dtdsModel.add(dtdModel)
            this.emitEvent()
            this.dtdsModel.leaveCriticalSection()
        }
    }

    override fun mergeDigitalTwinKnowledgeGraphUpdate(digitalTwinUri: DigitalTwinURI, dtkg: String) {
        this.dtkgsModel.enterCriticalSection(Lock.WRITE)
        val dtkgModel = RDFParser.fromString(dtkg, Lang.TTL)
            .build()
            .toModel()
            .mapLocalDigitalTwinModel()
        this.dtkgsModelMap[digitalTwinUri]?.also { this.dtkgsModel.remove(it) }
        this.dtkgsModelMap += (digitalTwinUri to dtkgModel)
        this.dtkgsModel.add(dtkgModel)
        this.emitEvent()
        this.dtkgsModel.leaveCriticalSection()
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

            this.emitEvent()
            true
        } else {
            false
        }

    private fun Model.toTurtle() = RDFWriter.create().lang(Lang.TTL).source(this).asString()
    private fun emitEvent() {
        CoroutineScope(dispatcher).launch { _platformKnowledgeGraphs.emit(platformKnowledgeGraphModel.toTurtle()) }
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

    companion object {
        private const val CONTENT_TYPE_CSV = "text/csv"
        private const val CONTENT_TYPE_TSV = "text/tab-separated-values"
        private const val CONTENT_TYPE_SPARQL_XML = "application/sparql-results+xml"
        private const val CONTENT_TYPE_SPARQL_JSON = "application/sparql-results+json"
    }
}
