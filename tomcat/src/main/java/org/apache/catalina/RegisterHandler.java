package org.apache.catalina;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;

public class RegisterHandler {

    public HttpResponse handle(HttpRequest request) {
        if (request.getMethod().equals("GET")) {
            return new HttpResponse(HttpStatusCode.OK, ContentType.HTML, "/register.html");
        }

        return new HttpResponse(HttpStatusCode.NOT_FOUND, ContentType.HTML, "/404.html"); // TODO: 405 페이지 필요
    }
}
