package org.apache.coyote.http11.web;

import nextstep.jwp.presentation.LoginController;
import nextstep.jwp.presentation.RegisterController;
import nextstep.jwp.presentation.dto.UserRegisterRequest;
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

        if (httpRequest.isMethod(HttpMethod.GET) && httpRequest.isUri("/register")) {
            return new RegisterController().render();
        }

        if (httpRequest.isMethod(HttpMethod.POST) && httpRequest.isUri("/register")) {
            final String requestBody = httpRequest.getRequestBody();
            final QueryParameters queryParameters = QueryParameters.from(requestBody);
            final UserRegisterRequest userRegisterRequest = UserRegisterRequest.from(queryParameters);
            return new RegisterController().register(userRegisterRequest);
        }

        return new HttpResponse(HttpStatus.OK, new HttpHeaders(new LinkedHashMap<>()), "Hello world!");
    }
}
