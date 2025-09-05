package org.apache.coyote.controller;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.ContentTypeSearcher;
import org.apache.coyote.FileManager;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler {

    private final static String STATIC_ROOT = "static";
    private static final Logger log = LoggerFactory.getLogger(LoginHandler.class);


    public static Http11Response getResponse(Http11Request reader) {
        String path = reader.getPath();

        int statusCode;
        if (reader.getRequestMethod().equals("GET")) {
            FileManager fileManager;
            try {
                fileManager = new FileManager(STATIC_ROOT + "/login.html", path);
            } catch (Exception e) {
                statusCode = 500;
                String body = "500 Internal Server Error";
                Http11Response response = new Http11Response(statusCode, "text/html; charset=utf-8",
                        body.getBytes().length, body);

                return response;
            }

            if (reader.getQueryParams().size() > 0) {
                String account = reader.getQueryParamValue("account");

                log.info("User:{}", InMemoryUserRepository.findByAccount(account).orElse(null).toString());
            }

            String contentType = ContentTypeSearcher.getContentTypeBy("/login.html");
            statusCode = 200;
            Http11Response response = new Http11Response(statusCode, contentType, fileManager.getContentLength(),
                    fileManager.getContentString());

            return response;

        }
        statusCode = 404;
        String body = "404 Not Found";
        Http11Response response = new Http11Response(statusCode, "text/html; charset=utf-8", body.getBytes().length,
                body);

        return response;
    }
}
