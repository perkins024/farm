/**
 * Copyright (c) 2016 Zerocracy
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
package com.zerocracy.radars.github;

import com.jcabi.github.Comment;
import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Issue;
import com.jcabi.github.Repo;
import com.zerocracy.jstk.Farm;
import java.io.IOException;
import java.util.Locale;
import javax.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

/**
 * Reaction on GitHub comment.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public final class ReOnComment implements Reaction {

    /**
     * GitHub client.
     */
    private final Github github;

    /**
     * Response.
     */
    private final Response response;

    /**
     * Ctor.
     * @param ghb Github client
     * @param rsp Response
     */
    public ReOnComment(final Github ghb, final Response rsp) {
        this.github = ghb;
        this.response = rsp;
    }

    @Override
    public void react(final Farm farm, final JsonObject event)
        throws IOException {
        final Comment.Smart comment = new Comment.Smart(this.comment(event));
        final String author = comment.author()
            .login().toLowerCase(Locale.ENGLISH);
        final String self = comment.issue().repo().github()
            .users().self().login().toLowerCase(Locale.ENGLISH);
        if (!author.equals(self)) {
            this.response.react(
                farm, comment
            );
        }
    }

    /**
     * The comment where it happened.
     * @param json JSON from GitHub
     * @return Comment
     */
    private Comment comment(final JsonObject json) {
        final JsonObject subject = json.getJsonObject("subject");
        final Repo repo = this.github.repos().get(
            new Coordinates.Simple(
                json.getJsonObject("repository").getString("full_name")
            )
        );
        final Issue issue = repo.issues().get(
            Integer.parseInt(
                StringUtils.substringAfterLast(
                    subject.getString("url"),
                    "/"
                )
            )
        );
        return new SafeComment(
            issue.comments().get(
                Integer.parseInt(
                    StringUtils.substringAfterLast(
                        subject.getString("latest_comment_url"),
                        "/"
                    )
                )
            )
        );
    }

}
