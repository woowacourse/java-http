package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.cookie.HttpCookie;
import org.apache.session.Session;
import org.apache.session.SessionManager;

import java.io.IOException;
import java.util.Map;

public class RegisterController extends AbstractController {

    private final StaticResourceHandler staticResourceHandler;

    public RegisterController(StaticResourceHandler staticResourceHandler) {
        this.staticResourceHandler = staticResourceHandler;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        staticResourceHandler.serve(request.getPathUri(), response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        if (isLogin(request.getHeader("Cookie"))) {
            response.status(HttpStatus.FORBIDDEN)
                    .contentType("plain")
                    .body("권한이 없습니다");
            return;
        }

        handleRegister(request, response);
    }

    private void handleRegister(HttpRequest request, HttpResponse response) {
        Map<String, String> requestBodyParams = request.parseQueryParams(request.getRequestBody());

        String account = requestBodyParams.get("account");
        String password = requestBodyParams.get("password");
        String email = requestBodyParams.get("email");

        if (isBlank(account) || isBlank(password) || isBlank(email)) {
            response.status(HttpStatus.BAD_REQUEST)
                    .contentType("plain")
                    .body("요청이 잘못되었습니다.");
            return;
        }

        InMemoryUserRepository.save(new User(account, password, email));
        response.redirectTo("/login");
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean isLogin(String cookie) {
        String jsessionId = HttpCookie.getJsessionId(cookie);

        if (jsessionId == null || jsessionId.isBlank()) {
            return false;
        }

        Session session = SessionManager.getInstance().findSession(jsessionId);
        return session != null && session.getAttribute("user") != null;
    }
}
