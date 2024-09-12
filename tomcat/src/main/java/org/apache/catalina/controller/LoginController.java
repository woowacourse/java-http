package org.apache.catalina.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.Http11Cookie;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11RequestBody;
import org.apache.coyote.http11.Http11RequestHeader;
import org.apache.coyote.http11.Http11Response;

public class LoginController extends AbstractController {

    private static final String LOGIN_HTML = "/login.html";
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String PARAM_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String EMPTY_STRING = "";
    private static final int OK_STATUS = 200;

    @Override
    protected void doGet(Http11Request request, Http11Response response) {
        Http11RequestHeader http11RequestHeader = request.getHttp11RequestHeader();
        String sessionId = http11RequestHeader.getCookie();
        Http11Cookie cookie = Http11Cookie.from(sessionId);
        if (!cookie.isJSessionIdEmpty()) {
            response.loginSuccess(cookie.getJSessionId());
            return;
        }
        response.setStaticResponse(request, LOGIN_HTML, OK_STATUS);
    }

    @Override
    protected void doPost(Http11Request request, Http11Response response) {
        Http11RequestBody http11RequestBody = request.getHttp11RequestBody();
        Map<String, String> loginData = parseBody(http11RequestBody.getBody());
        String account = loginData.get(ACCOUNT);
        String password = loginData.get(PASSWORD);

        processLogin(request, response, account, password);
    }

    private void processLogin(Http11Request request, Http11Response response, String account, String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty() || !user.get().checkPassword(password)) {
            response.unAuthorized(request);
            return;
        }

        String uuid = UUID.randomUUID().toString();
        saveSessionUser(uuid, account, user.get());
        response.loginSuccess(uuid);
    }

    private void saveSessionUser(String uuid, String account, User user) {
        Session session = new Session(uuid);
        session.setAttribute(account, user);
        SESSION_MANAGER.add(session);
    }

    private Map<String, String> parseBody(String body) {
        List<String> params = Arrays.asList(body.split(PARAM_DELIMITER));
        return params.stream()
                .map(param -> Arrays.asList(param.split(KEY_VALUE_DELIMITER)))
                .collect(Collectors.toMap(List::getFirst, this::getString));
    }

    private String getString(List<String> param) {
        if (param.size() > 1) {
            return param.get(1);
        }
        return EMPTY_STRING;
    }
}
