package org.apache.coyote.http11.controller;

import static org.apache.coyote.http11.response.StatusCode.*;

import java.util.Optional;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.apache.coyote.http11.util.FileReader;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class LoginController extends AbstractController {
    private static final FileReader fileReader = new FileReader();

    private static final String HTTP_VERSION_1_1 = "HTTP/1.1";
    private static final String UNAUTHORIZED_PATH = "/401.html";
    private static final String INDEX_PATH = "/index.html";
    private static final String LOGIN_PATH = "/login.html";
    private static final String JSESSIONID = "JSESSIONID";

    public LoginController() {
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        if (SessionManager.contains(request.getCookie(JSESSIONID))) {
            return redirect(INDEX_PATH);
        }
        return fileReader.readFile(LOGIN_PATH, HTTP_VERSION_1_1);
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        RequestBody requestBody = request.getRequestBody();
        Optional<User> user = InMemoryUserRepository.findByAccount(requestBody.get("account"));

        if (isLoginSuccess(user, requestBody)) {
            return login(user.get());
        }

        return redirect(UNAUTHORIZED_PATH);
    }

    private boolean isLoginSuccess(Optional<User> user, RequestBody requestBody) {
        if (user.isEmpty() || !requestBody.contains("password")) {
            return false;
        }

        User inputUser = user.get();
        String password = requestBody.get("password");

        return inputUser.checkPassword(password);
    }

    private HttpResponse login(User user) {
        Session session = new Session()
            .setAttribute("user", user);
        SessionManager.add(session);

        return redirect(INDEX_PATH)
            .addHeader("Set-Cookie", JSESSIONID + "=" + session.getId());
    }

    private HttpResponse redirect(String locationUri) {
        return HttpResponse.of(HTTP_VERSION_1_1, FOUND)
            .addHeader("Location", locationUri);
    }
}
