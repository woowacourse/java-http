package org.apache.coyote.http11.controller;

import static org.apache.coyote.http11.response.StatusCode.*;

import java.util.Optional;

import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.util.FileReader;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class LoginController extends AbstractController {
    private static final FileReader fileReader = new FileReader();

    private static final String HTTP_VERSION_1_1 = "HTTP/1.1";

    private static final String UNAUTHORIZED_HTML = "/401.html";
    private static final String INDEX_HTML = "/index.html";
    private static final String LOGIN_PATH = "/login.html";

    public LoginController() {
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) {
        if (SessionManager.contains(request.getCookie("JSESSIONID"))) {
            return redirect(INDEX_HTML);
        }
        return fileReader.readFile(LOGIN_PATH, HTTP_VERSION_1_1);
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        RequestBody requestBody = request.getRequestBody();
        Optional<User> user = InMemoryUserRepository.findByAccount(requestBody.get("account"));

        if (loginSuccess(user, requestBody)) {
            Session session = new Session()
                .setAttribute("user", user);
            SessionManager.add(session);

            return redirect(INDEX_HTML)
                .addHeader("Set-Cookie", "JSESSIONID=" + session.getId());
        }

        return redirect(UNAUTHORIZED_HTML);
    }

    private boolean loginSuccess(Optional<User> user, RequestBody requestBody) {
        if (user.isEmpty() || !requestBody.contains("password")) {
            return false;
        }

        User loginUser = user.get();
        String password = requestBody.get("password");

        return loginUser.checkPassword(password);
    }

    private HttpResponse redirect(String locationUri) {
        return HttpResponse.of(HTTP_VERSION_1_1, FOUND)
            .addHeader("Location", locationUri);
    }
}
