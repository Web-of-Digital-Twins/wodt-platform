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

package architecture

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import io.kotest.core.spec.style.StringSpec

class CleanArchitectureTest : StringSpec({
    "Test that the layers of the Clean Architecture are respected" {
        layeredArchitecture()
            .consideringAllDependencies()
            .layer("entity").definedBy("..entity..")
            .layer("application").definedBy("..application..")
            .layer("infrastructure").definedBy("..infrastructure..")
            .whereLayer("entity").mayOnlyBeAccessedByLayers("application", "infrastructure")
            .whereLayer("application").mayOnlyBeAccessedByLayers("infrastructure")
            .whereLayer("infrastructure").mayNotBeAccessedByAnyLayer()
            .check(
                ClassFileImporter()
                    .withImportOption { !it.contains("/test/") } // ignore tests classes
                    .importPackages("entity", "application", "infrastructure"),
            )
    }
})
