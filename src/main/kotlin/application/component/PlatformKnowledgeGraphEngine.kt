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

package application.component

import entity.digitaltwin.DigitalTwinDescription
import entity.digitaltwin.DigitalTwinURI
import kotlinx.coroutines.flow.Flow

/**
 * This component models the reader part of the WoDT Platform Knowledge Graph Engine component
 * of the Abstract Architecture.
 */
interface PlatformKnowledgeGraphEngineReader {
    /** Obtain the flow of Platform Knowledge Graphs emitted by the component. */
    val platformKnowledgeGraphs: Flow<String>

    /** Obtain the current status of the Platform Knowledge Graph. */
    fun currentPlatformKnowledgeGraph(): String?

    /** Obtain the cached current status of a registered WoDT Digital Twin identified by its [dtUri]. */
    fun currentCachedDigitalTwinKnowledgeGraph(dtUri: DigitalTwinURI): String?

    /**
     * Query the Platform Knowledge Graph. The query will be returned in the [responseContentType]
     * if possible.
     */
    fun query(query: String, responseContentType: String?): String?

    /** Get the WoDT Digital Twins that are associated with a specific [physicalAssetId]. */
    fun getDigitalTwinsFromPhysicalAsset(physicalAssetId: String): Set<DigitalTwinURI>
}

/**
 * This component models the writer part of the WoDT Digital Twins Platform Knowledge Graph Engine component
 * of the Abstract Architecture.
 */
interface PlatformKnowledgeGraphEngineWriter {
    /**
     * Merge the Digital Twin Description of a WoDT Digital Twin to the current Platform
     * Knowledge Graph.
     */
    fun mergeDigitalTwinDescription(dtd: DigitalTwinDescription)

    /**
     * Merge the updated [dtkg] of the registered WoDT Digital Twin identified via its [dtUri].
     * The [digitalTwinUri] is the original one, and not the mapped one.
     */
    fun mergeDigitalTwinKnowledgeGraphUpdate(digitalTwinUri: DigitalTwinURI, dtkg: String)

    /**
     * Delete the Digital Twin identified by its [digitalTwinUri].
     * The [digitalTwinUri] is the original one, and not the mapped one.
     */
    fun deleteDigitalTwin(digitalTwinUri: DigitalTwinURI): Boolean
}

/**
 * This interface models the general WoDT Digital Twins Platform Knowledge Graph Engine component of the Abstract
 * Architecture.
 */
interface PlatformKnowledgeGraphEngine : PlatformKnowledgeGraphEngineReader, PlatformKnowledgeGraphEngineWriter
