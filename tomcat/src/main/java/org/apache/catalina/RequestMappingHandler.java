package org.apache.catalina;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;

public class RequestMappingHandler {

    public HttpResponse request(HttpRequest request) {
        if (request.getPath().endsWith(".css")) {
            return new HttpResponse(HttpStatusCode.OK, ContentType.CSS, request.getPath());
        }
        if (request.getPath().endsWith(".js")) {
            return new HttpResponse(HttpStatusCode.OK, ContentType.JAVASCRIPT, request.getPath());
        }
        if (request.getMethod().equals("GET") && request.getPath().equals("/login")) {
            LoginHandler loginHandler = new LoginHandler();
            return loginHandler.login(request);
        }
        if (request.getPath().equals("/register")) {
            RegisterHandler registerHandler = new RegisterHandler();
            return registerHandler.handle(request);
        }

        return new HttpResponse(HttpStatusCode.NOT_FOUND, ContentType.HTML, "/404.html");
    }
}
