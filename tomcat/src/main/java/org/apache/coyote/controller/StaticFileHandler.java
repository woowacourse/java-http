package org.apache.coyote.controller;

import org.apache.coyote.ContentTypeSearcher;
import org.apache.coyote.FileManager;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.session.SessionManager;

public class StaticFileHandler extends AbstractController {

    private final static String STATIC_ROOT = "static";
    private final static SessionManager sessionManger = SessionManager.getInstance();

    @Override
    public void service(Http11Request reader, Http11Response response) {
        String path = reader.getPath();
        HttpStatus statusCode;

        FileManager fileManager;
        try {
            fileManager = new FileManager(STATIC_ROOT + path, path);
        } catch (IllegalArgumentException e) {
            statusCode = HttpStatus.NOTFOUND;
            String body = "404 Not Found";
            response.status(statusCode);
            response.contentType("text/html; charset=utf-8");
            response.body(body.getBytes());

            return;
        } catch (Exception e) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
            String body = "500 Internal Server Error";
            response.status(statusCode);
            response.contentType("text/html; charset=utf-8");
            response.body(body.getBytes());

            return;
        }

        String contentType = ContentTypeSearcher.getContentTypeBy(path);
        statusCode = HttpStatus.OK;
        response.status(statusCode);
        response.contentType(contentType);
        response.body(fileManager.getContent());
    }
}
