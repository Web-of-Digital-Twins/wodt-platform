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

plugins {
    id("org.danilopianini.gradle-pre-commit-git-hooks") version "2.0.16"
    id("com.gradle.develocity") version("3.19")
}

rootProject.name = "wodt-platform"

develocity {
    buildScan {
        termsOfUseUrl.set("https://gradle.com/help/legal-terms-of-use")
        termsOfUseAgree.set("yes")
        publishing.onlyIf { it.buildResult.failures.isNotEmpty() } // Publish the build scan when the build fails
    }
}

gitHooks {
    preCommit {
        tasks("detekt")
        tasks("ktlintCheck")
    }

    commitMsg {
        conventionalCommits()
    }

    hook("post-commit") {
        from {
            "git verify-commit HEAD &> /dev/null; " +
                "if (( $? == 1 )); then echo -e '\\033[0;31mWARNING(COMMIT UNVERIFIED): commit NOT signed\\033[0m';" +
                "else echo -e '\\033[0;32mOK COMMIT SIGNED\\033[0m'; fi"
        }
    }

    createHooks(true)
}
