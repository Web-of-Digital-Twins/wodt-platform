import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeExactly

/*
 * Copyright (c) 2023. Smart Operating Block
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

class TestApp : StringSpec({
    "2+2 is equal to 4" {
        2 + 2 shouldBeExactly 4
    }
})
