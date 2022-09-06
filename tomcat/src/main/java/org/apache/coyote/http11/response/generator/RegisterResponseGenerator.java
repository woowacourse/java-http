package org.apache.coyote.http11.response.generator;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterResponseGenerator implements ResponseGenerator {

    private static final String REGISTER_SUCCESS_REDIRECT_URI = "http://localhost:8080/index.html";

    @Override
    public boolean isSuitable(HttpRequest httpRequest) {
        return httpRequest.isRegisterRequest();
    }

    @Override
    public HttpResponse generate(HttpRequest httpRequest) throws IOException {
        User user = new User(
                httpRequest.getParamValueOf("account"),
                httpRequest.getParamValueOf("password"),
                httpRequest.getParamValueOf("email")
        );
        InMemoryUserRepository.save(user);
        return HttpResponse.found(REGISTER_SUCCESS_REDIRECT_URI);
    }
}
