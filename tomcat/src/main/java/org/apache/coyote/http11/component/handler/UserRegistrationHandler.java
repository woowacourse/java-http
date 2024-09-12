package org.apache.coyote.http11.component.handler;

import org.apache.coyote.http11.component.common.Method;
import org.apache.coyote.http11.component.request.HttpRequest;
import org.apache.coyote.http11.component.resource.StaticResourceFinder;
import org.apache.coyote.http11.component.response.HttpResponse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class UserRegistrationHandler implements HttpHandler {

    private static final String REGISTER_SUCCESSFUL_REDIRECT_URI = "http://localhost:8080/index.html";
    private static final String ID_BODY_NAME = "account";
    private static final String EMAIL_BODY_NAME = "email";
    private static final String PASSWORD_BODY_NAME = "password";

    private final String path;

    public UserRegistrationHandler(final String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public HttpResponse handle(final HttpRequest request) {
        if (request.isSameMethod(Method.POST)) {
            final var email = request.getBodyContent(EMAIL_BODY_NAME);
            final var account = request.getBodyContent(ID_BODY_NAME);
            final var password = request.getBodyContent(PASSWORD_BODY_NAME);
            return doPost(email, account, password);
        }

        return doGet();
    }

    private HttpResponse doGet() {
        return StaticResourceFinder.render("register.html");
    }

    private HttpResponse doPost(final String email, final String account, final String password) {
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new IllegalStateException("이미 존재하는 회원 ID");
        }
        InMemoryUserRepository.save(new User(account, password, email));
        return StaticResourceFinder.renderRedirect(REGISTER_SUCCESSFUL_REDIRECT_URI);
    }
}
