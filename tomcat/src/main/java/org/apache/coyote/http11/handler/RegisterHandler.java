package org.apache.coyote.http11.handler;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.ResourceReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.util.Parser;

import java.io.IOException;
import java.util.Map;

public class RegisterHandler {

    private static final String INDEX = "/index.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String STATIC_PATH = "static";
    private static final String REGISTER_PATH = "/register.html";

    private RegisterHandler() {
    }

    public static HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        final HttpMethod method = httpRequest.getMethod();

        if (method == HttpMethod.POST) {
            return register(httpRequest);
        }

        final String responseBody = ResourceReader.readResource(STATIC_PATH + REGISTER_PATH);
        return HttpResponse.okResponse(httpRequest, responseBody);
    }


    private static HttpResponse register(final HttpRequest httpRequest) {
        final Map<String, String> requestParam = Parser.queryParamParse(httpRequest.getRequestBody());
        final User registeredUser = new User(requestParam.get(ACCOUNT), requestParam.get(PASSWORD), requestParam.get(EMAIL));
        InMemoryUserRepository.save(registeredUser);
        return HttpResponse.foundResponse(httpRequest,INDEX);
    }
}
