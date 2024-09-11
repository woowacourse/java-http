package org.apache.coyote.handler;

import org.apache.coyote.NotFoundException;
import org.apache.http.HttpMethod;
import org.apache.http.request.HttpRequest;
import org.apache.http.request.RequestLine;
import org.apache.http.response.HttpResponseGenerator;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterHandler extends Handler {
    private static final RegisterHandler INSTANCE = new RegisterHandler();

    private RegisterHandler() {
    }

    public static RegisterHandler getInstance() {
        return INSTANCE;
    }

    public String handle(final HttpRequest httpRequest) {
        if (httpRequest.isSameMethod(HttpMethod.GET)) {
            final RequestLine requestLine = RequestLine.from("GET /register.html HTTP/1.1");
            return StaticResourceHandler.getInstance().handle(new HttpRequest(requestLine, null, null));
        }

        if (httpRequest.isSameMethod(HttpMethod.POST)) {
            return processRegisterPostRequest(httpRequest);
        }

        throw new NotFoundException("페이지를 찾을 수 없습니다.");
    }

    private String processRegisterPostRequest(final HttpRequest httpRequest) {
        String account = httpRequest.getFormBodyByKey("account");
        String email = httpRequest.getFormBodyByKey("email");
        String password = httpRequest.getFormBodyByKey("password");
        InMemoryUserRepository.save(new User(account, password, email));
        return HttpResponseGenerator.getFoundResponse("/index.html");
    }
}
