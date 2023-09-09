package org.apache.coyote.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.Controller;
import org.apache.coyote.controller.util.FileResolver;
import org.apache.coyote.http11.http.message.HttpRequest;
import org.apache.coyote.http11.http.util.HttpMethod;

import java.io.IOException;
import java.util.Map;

public class RegisterController extends Controller {

    public String run(final HttpRequest request) throws IOException {
        final String method = request.getMethod();
        if (HttpMethod.GET.isSameMethod(method)) {
            return new FileController().createResponse(FileResolver.REGISTER);
        }
        if (HttpMethod.POST.isSameMethod(method)) {
            final Map<String, String> body = request.getBody();
            final User user = new User(body.get("account"), body.get("password"), body.get("email"));
            InMemoryUserRepository.save(user);
            return createRedirectResponse(FileResolver.INDEX_HTML);
        }
        throw new IllegalArgumentException("잘못된 메소드 형식입니다.");
    }
}
