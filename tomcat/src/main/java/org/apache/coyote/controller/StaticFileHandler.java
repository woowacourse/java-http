package org.apache.coyote.controller;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.ContentTypeSearcher;
import org.apache.coyote.FileManager;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.session.SessionManager;

public class StaticFileHandler {

    private final static String STATIC_ROOT = "static";

    public static Http11Response getResponse(Http11Request reader, SessionManager sessionManager) {
        String path = reader.getPath();
        HttpStatusCode statusCode;

        FileManager fileManager;
        try {
            fileManager = new FileManager(STATIC_ROOT + path, path);
        } catch (IllegalArgumentException e) {
            statusCode = HttpStatusCode.NOTFOUND;
            String body = "404 Not Found";
            Http11Response response = new Http11Response(
                    statusCode,
                    "text/html; charset=utf-8",
                    body.getBytes(StandardCharsets.UTF_8),
                    null,
                    null
            );

            return response;
        } catch (Exception e) {
            statusCode = HttpStatusCode.INTERNAL_SERVER_ERROR;
            String body = "500 Internal Server Error";
            Http11Response response = new Http11Response(
                    statusCode,
                    "text/html; charset=utf-8",
                    body.getBytes(StandardCharsets.UTF_8),
                    null,
                    null
            );

            return response;
        }

        String contentType = ContentTypeSearcher.getContentTypeBy(path);
        statusCode = HttpStatusCode.OK;
        Http11Response response = new Http11Response(statusCode,
                contentType,
                fileManager.getContent(),
                null,
                null);

        return response;
    }
}
