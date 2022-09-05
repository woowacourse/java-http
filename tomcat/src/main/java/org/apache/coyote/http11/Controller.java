package org.apache.coyote.http11;

import static org.apache.coyote.http11.ViewResolver.staticFileRequest;

import java.util.Map;

import org.apache.coyote.http11.HttpResponse.Builder;

import nextstep.jwp.exception.InvalidLoginRequestException;
import nextstep.jwp.handler.LoginHandler;
import nextstep.jwp.model.User;

public class Controller {

    public HttpResponse performBasicUrl(HttpRequest request) {
        String contentType = FileExtension.HTML.getContentType();

        return new Builder()
                .statusCode(HttpStatus.OK)
                .header("Content-Type", contentType)
                .responseBody("Hello world!")
                .build();
    }

    public HttpResponse performLoginRequest(HttpRequest request) {
        Map<String, String> queries = UriParser.parseUri(request.getUri());
        if (queries.isEmpty()) {
            return staticFileRequest("/login.html");
        }
        return performLogin(queries);
    }

    private HttpResponse performLogin(Map<String, String> queries) {
        try {
            LoginHandler loginHandler = new LoginHandler();
            User user = loginHandler.login(queries);

            return staticFileRequest("/index.html");
        } catch (InvalidLoginRequestException e) {
            return new HttpResponse.Builder()
                    .statusCode(HttpStatus.NOT_FOUND)
                    .build();
        }
    }

    public HttpResponse returnNotFountResponse(HttpRequest request) {
        return new HttpResponse.Builder()
                .statusCode(HttpStatus.NOT_FOUND)
                .build();
    }
}
