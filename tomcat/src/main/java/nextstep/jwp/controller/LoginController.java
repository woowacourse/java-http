package nextstep.jwp.controller;

import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.request.Request;
import org.apache.coyote.http11.message.response.Response;

public class LoginController extends AbstractController {

    private static final String INDEX_TEMPLATE = "index.html";
    private static final String USER = "user";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String LOGIN_TEMPLATE = "login.html";
    private static final String JSESSIONID = "JSESSIONID";

    @Override
    protected void doPost(final Request request, final Response response) {
        final Map<String, String> requestForms = request.getRequestForms().getFormData();
        final Optional<User> user = login(requestForms.get(ACCOUNT), requestForms.get(PASSWORD));
        if (user.isPresent()) {
            loginSuccess(request, response, user.get());
            return;
        }
        loginFail(response);
    }

    private void loginSuccess(final Request request, final Response response, final User user) {
        response.setLocation(INDEX_TEMPLATE);
        response.setStatus(HttpStatus.FOUND);

        if (request.noSession()) {
            final Session session = new Session();
            session.setAttribute(USER, user);
            SessionManager.add(session);
            response.addCookie(JSESSIONID, session.getId());
        }
    }

    private void loginFail(final Response response) {
        final Response failedResponse = Response.createByTemplate(HttpStatus.UNAUTHORIZED, "401.html");
        response.setBy(failedResponse);
    }

    private Optional<User> login(final String account, final String password) {
        return InMemoryUserRepository.findByAccountAndPassword(account, password);
    }

    @Override
    protected void doGet(final Request request, final Response response) throws Exception {
        if (request.getSessionValue(USER) != Optional.empty()) {
            response.setLocation(INDEX_TEMPLATE);
            response.setStatus(HttpStatus.FOUND);
            return;
        }
        final Response createdResponse = Response.createByTemplate(HttpStatus.OK, LOGIN_TEMPLATE);
        response.setBy(createdResponse);
    }
}
