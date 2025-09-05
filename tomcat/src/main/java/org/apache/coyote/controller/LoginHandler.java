package org.apache.coyote.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.ContentTypeSearcher;
import org.apache.coyote.FileManager;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler {

    private static final String STATIC_ROOT = "static";
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
                Http11Response response = new Http11Response(statusCode,
                        "text/html; charset=utf-8",
                        body.getBytes(StandardCharsets.UTF_8).length,
                        body.getBytes(StandardCharsets.UTF_8));

                return response;
            }

            if (reader.getQueryParams().size() > 0) {
                String account = reader.getQueryParamValue("account");
                User user = InMemoryUserRepository.findByAccount(account)
                        .orElseThrow(() -> new IllegalArgumentException("user not exist"));
                log.info("User:{}", user.toString());
            }

            String contentType = ContentTypeSearcher.getContentTypeBy("/login.html");
            statusCode = 200;
            Http11Response response = new Http11Response(statusCode,
                    contentType,
                    fileManager.getContentLength(),
                    fileManager.getContent());

            return response;

        }
        statusCode = 404;
        String body = "404 Not Found";
        Http11Response response = new Http11Response(statusCode,
                "text/html; charset=utf-8",
                body.getBytes(StandardCharsets.UTF_8).length,
                body.getBytes(StandardCharsets.UTF_8));

        return response;
    }
}
