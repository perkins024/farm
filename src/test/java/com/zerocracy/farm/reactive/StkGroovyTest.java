/**
 * Copyright (c) 2016-2017 Zerocracy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to read
 * the Software only. Permissions is hereby NOT GRANTED to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.zerocracy.farm.reactive;

import com.zerocracy.jstk.Project;
import com.zerocracy.jstk.fake.FkProject;
import com.zerocracy.pm.ClaimIn;
import com.zerocracy.pm.Claims;
import java.util.AbstractMap;
import org.cactoos.io.InputOf;
import org.cactoos.list.StickyMap;
import org.cactoos.text.TextAsBytes;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link StkGroovy}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.12
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class StkGroovyTest {

    @Test
    public void parsesGroovy() throws Exception {
        final Project project = new FkProject();
        new StkGroovy(
            new InputOf(
                new TextAsBytes(
                    String.join(
                        "\n",
                        "import com.zerocracy.pm.ClaimOut",
                        "def exec(project, xml) {",
                        "new ClaimOut()",
                        "  .type(binding.variables.dep)",
                        "  .postTo(project)",
                        "}"
                    )
                )
            ),
            "script",
            new StickyMap<String, Object>(
                new AbstractMap.SimpleEntry<>(
                    "dep", "hello dude"
                )
            )
        ).process(project, null);
        try (final Claims claims = new Claims(project).lock()) {
            MatcherAssert.assertThat(
                new ClaimIn(claims.iterate().iterator().next()).type(),
                Matchers.endsWith(" dude")
            );
        }
    }
}
