package org.apache.coyote.controller;

import org.apache.coyote.ContentTypeSearcher;
import org.apache.coyote.FileManager;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;

public class StaticFileHandler {

    private final static String STATIC_ROOT = "static";

    public static Http11Response getResponse(Http11Request reader) {
        String path = reader.getPath();
        int statusCode;

        FileManager fileManager;
        try {
            fileManager = new FileManager(STATIC_ROOT + path, path);
        } catch (IllegalArgumentException e) {
            statusCode = 404;
            String body = "404 Not Found";
            Http11Response response = new Http11Response(
                    statusCode,
                    "text/html; charset=utf-8",
                    body.getBytes().length,
                    body
            );

            return response;
        } catch (Exception e) {
            statusCode = 500;
            String body = "500 Internal Server Error";
            Http11Response response = new Http11Response(
                    statusCode,
                    "text/html; charset=utf-8",
                    body.getBytes().length,
                    body
            );

            return response;
        }

        String contentType = ContentTypeSearcher.getContentTypeBy(path);
        statusCode=200;
        Http11Response response = new Http11Response(statusCode, contentType, fileManager.getContentLength(),
                fileManager.getContentString());

        return response;
    }
}
