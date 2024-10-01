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

import TestingUtils.readResourceFile
import application.component.PlatformKnowledgeGraphEngine
import application.presenter.dtd.DigitalTwinDescriptorDeserialization.toDTD
import application.service.EcosystemRegistryService
import entity.digitaltwin.DigitalTwinURI
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class JenaPlatformKnowledgeGraphEngineTest : StringSpec({
    val testPort = 4000
    val dtUri = DigitalTwinURI("https://example.com/dt")
    val selectQuery = """
            SELECT ?subject ?predicate ?object
            WHERE {
              ?subject ?predicate ?object
            }
            LIMIT 10
    """.trimIndent()
    val askQuery = """
        ASK
        WHERE {
          ?subject ?predicate <http://example.com/Person>
        }
    """.trimIndent()
    val constructQuery = """
        CONSTRUCT {
          ?subject <http://example.it/is> <http://example.com/Person>;
        }
        WHERE {
          ?subject ?predicate <http://example.com/Person>.
        }
        LIMIT 10
    """.trimIndent()
    val describeQuery = """
        DESCRIBE <${dtUri.uri}>
    """.trimIndent()
    val sparqlUpdateQuery = """
        PREFIX foaf: <http://xmlns.com/foaf/0.1/>

        INSERT DATA {
          <http://example.org/johndoe> foaf:name "John Doe" .
          <http://example.org/johndoe> rdf:type foaf:Person .
        }
    """.trimIndent()

    fun insertDTD(platformKnowledgeGraphEngine: PlatformKnowledgeGraphEngine, fileName: String = "wotDtd.json") {
        readResourceFile(fileName)?.toDTD("application/td+json")?.run {
            platformKnowledgeGraphEngine.mergeDigitalTwinDescriptor(this)
        }
    }

    fun insertDTKG(
        platformKnowledgeGraphEngine: PlatformKnowledgeGraphEngine,
        digitalTwinURI: DigitalTwinURI = dtUri,
        fileName: String = "dtkgWithRelationship.ttl",
    ): String = readResourceFile(fileName)?.also {
        platformKnowledgeGraphEngine.mergeDigitalTwinKnowledgeGraphUpdate(digitalTwinURI, it)
    }.orEmpty()

    "it should be possible to merge a Digital Twin Descriptor" {
        val platformKnowledgeGraphEngine = JenaPlatformKnowledgeGraphEngine(EcosystemRegistryService(testPort))
        platformKnowledgeGraphEngine.currentPlatformKnowledgeGraph()?.isEmpty() shouldBe true
        insertDTD(platformKnowledgeGraphEngine)
        platformKnowledgeGraphEngine.currentPlatformKnowledgeGraph()?.isEmpty() shouldBe false
    }

    "it should be possible to merge a Digital Twin Knowledge Graph" {
        val platformKnowledgeGraphEngine = JenaPlatformKnowledgeGraphEngine(EcosystemRegistryService(testPort))
        platformKnowledgeGraphEngine.currentPlatformKnowledgeGraph()?.isEmpty() shouldBe true
        val dtkg = insertDTKG(platformKnowledgeGraphEngine)
        platformKnowledgeGraphEngine.currentPlatformKnowledgeGraph() shouldBe dtkg
    }

    "it should be possible to delete a registered WoDT Digital Twin" {
        val ecosystemRegistry = EcosystemRegistryService(testPort)
        val platformKnowledgeGraphEngine = JenaPlatformKnowledgeGraphEngine(ecosystemRegistry)
        ecosystemRegistry.signalRegistration(dtUri)
        insertDTD(platformKnowledgeGraphEngine)
        insertDTKG(platformKnowledgeGraphEngine)
        platformKnowledgeGraphEngine.deleteDigitalTwin(dtUri) shouldBe true
        platformKnowledgeGraphEngine.currentPlatformKnowledgeGraph()?.isEmpty() shouldBe true
    }

    "it should not be possible to delete a not-registered Digital Twin" {
        val platformKnowledgeGraphEngine = JenaPlatformKnowledgeGraphEngine(EcosystemRegistryService(testPort))
        platformKnowledgeGraphEngine.deleteDigitalTwin(dtUri) shouldBe false
    }

    "it should be possible to obtain the cached representation of a registered WoDT Digital Twin" {
        val ecosystemRegistry = EcosystemRegistryService(testPort)
        val platformKnowledgeGraphEngine = JenaPlatformKnowledgeGraphEngine(ecosystemRegistry)
        ecosystemRegistry.signalRegistration(dtUri)
        insertDTKG(platformKnowledgeGraphEngine)
        readResourceFile("mappedDtkgWithRelationship.ttl")?.run {
            platformKnowledgeGraphEngine.currentCachedDigitalTwinKnowledgeGraph(dtUri) shouldBe this
        }
    }

    "it should be possible to obtain all the registered WoDT Digital Twins that are associated with " +
        "a specific Physical Asset" {
            val ecosystemRegistry = EcosystemRegistryService(testPort)
            val platformKnowledgeGraphEngine = JenaPlatformKnowledgeGraphEngine(ecosystemRegistry)
            ecosystemRegistry.signalRegistration(dtUri)
            insertDTD(platformKnowledgeGraphEngine)
            platformKnowledgeGraphEngine.getDigitalTwinsFromPhysicalAsset("lampPA")
                .contains(dtUri) shouldBe true
        }

    "only registered DT URIs should be mapped to local urls" {
        val ecosystemRegistry = EcosystemRegistryService(testPort)
        val platformKnowledgeGraphEngine = JenaPlatformKnowledgeGraphEngine(ecosystemRegistry)
        ecosystemRegistry.signalRegistration(dtUri)
        insertDTD(platformKnowledgeGraphEngine)
        insertDTKG(platformKnowledgeGraphEngine)
        readResourceFile("mappedDtkgWithRelationship.ttl")?.run {
            platformKnowledgeGraphEngine.currentCachedDigitalTwinKnowledgeGraph(dtUri) shouldBe this
        }
    }

    "when register a new DT old relationships with it should be mapped to local URLs" {
        val dtToRegisterLater = DigitalTwinURI("http://example.com/intersection")
        val ecosystemRegistry = EcosystemRegistryService(testPort)
        val platformKnowledgeGraphEngine = JenaPlatformKnowledgeGraphEngine(ecosystemRegistry)
        ecosystemRegistry.signalRegistration(dtUri)
        insertDTD(platformKnowledgeGraphEngine)
        insertDTKG(platformKnowledgeGraphEngine)
        readResourceFile("mappedDtkgWithRelationship.ttl")?.run {
            platformKnowledgeGraphEngine.currentCachedDigitalTwinKnowledgeGraph(dtUri) shouldBe this
        }
        ecosystemRegistry.signalRegistration(dtToRegisterLater)
        insertDTD(platformKnowledgeGraphEngine, "wotDtdInRelationship.json")
        insertDTKG(platformKnowledgeGraphEngine, dtToRegisterLater, "dtkgInRelationship.ttl")
        readResourceFile("mappedDtkgWithRelationshipMapped.ttl")?.run {
            platformKnowledgeGraphEngine.currentCachedDigitalTwinKnowledgeGraph(dtUri) shouldBe this
        }
    }

    listOf(
        "application/sparql-results+xml",
        "application/sparql-results+json",
        "text/csv",
        "text/tab-separated-values",
    ).forEach {
        "it should be possible to perform SPARQL SELECT Queries using the $it content-type" {
            val ecosystemRegistry = EcosystemRegistryService(testPort)
            val platformKnowledgeGraphEngine = JenaPlatformKnowledgeGraphEngine(ecosystemRegistry)
            platformKnowledgeGraphEngine.query(
                selectQuery,
                it,
            ) shouldNotBe null
        }
    }

    listOf(
        "application/sparql-results+xml",
        "application/sparql-results+json",
    ).forEach {
        "it should be possible to perform SPARQL ASK Queries using $it content-type" {
            val ecosystemRegistry = EcosystemRegistryService(testPort)
            val platformKnowledgeGraphEngine = JenaPlatformKnowledgeGraphEngine(ecosystemRegistry)
            platformKnowledgeGraphEngine.query(
                askQuery,
                it,
            ) shouldNotBe null
        }
    }

    "it should be possible to perform SPARQL CONSTRUCT Queries" {
        val ecosystemRegistry = EcosystemRegistryService(testPort)
        val platformKnowledgeGraphEngine = JenaPlatformKnowledgeGraphEngine(ecosystemRegistry)
        platformKnowledgeGraphEngine.query(constructQuery, null) shouldNotBe null
    }

    "it should be possible to perform SPARQL DESCRIBE Queries" {
        val ecosystemRegistry = EcosystemRegistryService(testPort)
        val platformKnowledgeGraphEngine = JenaPlatformKnowledgeGraphEngine(ecosystemRegistry)
        platformKnowledgeGraphEngine.query(describeQuery, null) shouldNotBe null
    }

    "it should not be possible to perform SPARQL UPDATE queries" {
        val ecosystemRegistry = EcosystemRegistryService(testPort)
        val platformKnowledgeGraphEngine = JenaPlatformKnowledgeGraphEngine(ecosystemRegistry)
        platformKnowledgeGraphEngine.query(sparqlUpdateQuery, null) shouldBe null
    }
})
