package org.apache.coyote.http11;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.response.StartLine;
import org.apache.coyote.http11.response.StatusCode;

public class ResponseGenerator {

    private ResponseGenerator() {
    }

    public static String generate(final Request request) {
        final StartLine startLine = new StartLine(HttpVersion.HTTP_1_1, StatusCode.OK);
        final ContentType contentType = ContentType.HTML;
        final String responseBody = "Hello world!";

        final Response response = Response.of(startLine, contentType, responseBody);

        return response.toMessage();
    }
}
