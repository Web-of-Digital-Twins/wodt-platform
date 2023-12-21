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

package entity.ontology

/**
 * This module wraps the needed elements of the WoDT vocabulary.
 */
object WoDTVocabulary {
    /** Base URI of the vocabulary. */
    const val BASE_URI = "https://purl.org/wodt/"

    /** Physical Asset id predicate. */
    const val PHYSICAL_ASSET_ID = BASE_URI + "physicalAssetId"
}
