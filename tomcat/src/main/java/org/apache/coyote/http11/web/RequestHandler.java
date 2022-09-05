package org.apache.coyote.http11.web;

import nextstep.jwp.presentation.LoginController;
import org.apache.coyote.http11.support.HttpHeaders;
import org.apache.coyote.http11.support.HttpMethod;
import org.apache.coyote.http11.support.HttpStatus;
import org.apache.coyote.http11.web.request.HttpRequest;
import org.apache.coyote.http11.web.response.HttpResponse;
import java.io.IOException;
import java.util.LinkedHashMap;

public class RequestHandler {

    public HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.isMethod(HttpMethod.GET) && httpRequest.isUri("/login")) {
            final QueryParameters queryParameters = httpRequest.getQueryParameters();
            return new LoginController().login(queryParameters);
        }

        return new HttpResponse(HttpStatus.OK, new HttpHeaders(new LinkedHashMap<>()), "Hello world!");
    }
}
