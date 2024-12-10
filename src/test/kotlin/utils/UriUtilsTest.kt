/*
 * Copyright (c) 2024. Andrea Giulianelli
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

package utils

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import utils.UriUtil.relativeResolve
import java.net.URI

class UriUtilsTest : StringSpec({
    "It should be possible to resolve a simple URI with a relative one" {
        URI.create("http://example.com").relativeResolve("dtkg") shouldBe URI.create("http://example.com/dtkg")
    }

    "It should be possible to resolve an URI with already a path with a relative one" {
        URI.create("http://example.com/a").relativeResolve("dtkg") shouldBe URI.create("http://example.com/a/dtkg")
        URI.create("http://example.com/a/b/c").relativeResolve("dtkg") shouldBe
            URI.create("http://example.com/a/b/c/dtkg")
    }

    "It should be possible to resolve an URI with already a path with a relative one independently of fragment" {
        URI.create("http://example.com#").relativeResolve("dtkg") shouldBe URI.create("http://example.com/dtkg")
        URI.create("http://example.com/a/b/c#").relativeResolve("dtkg") shouldBe
            URI.create("http://example.com/a/b/c/dtkg")
    }

    "It should be possible to append a fragment to base uri" {
        URI.create("http://example.com").relativeResolve("#dtkg") shouldBe URI.create("http://example.com#dtkg")
        URI.create("http://example.com/a/b/c").relativeResolve("#dtkg") shouldBe
            URI.create("http://example.com/a/b/c#dtkg")
        URI.create("http://example.com/a/b/c").relativeResolve("d/e/f#dtkg") shouldBe
            URI.create("http://example.com/a/b/c/d/e/f#dtkg")
    }

    "It should be possible to append at the end even when the relative path starts with /" {
        URI.create("http://example.com").relativeResolve("/dtkg") shouldBe URI.create("http://example.com/dtkg")
        URI.create("http://example.com/a/b/c").relativeResolve("/dtkg") shouldBe
            URI.create("http://example.com/a/b/c/dtkg")
        URI.create("http://example.com/a/b/c").relativeResolve("/path/to/dtkg") shouldBe
            URI.create("http://example.com/a/b/c/path/to/dtkg")
    }
})
