package org.apache.catalina.controller;

import static org.apache.catalina.controller.IndexController.INDEX;
import static org.apache.coyote.http11.cookie.Cookie.KEY_VALUE_DELIMITER;
import static org.apache.coyote.http11.request.HttpMethod.GET;
import static org.apache.coyote.http11.request.HttpMethod.POST;
import static org.apache.coyote.http11.response.StatusCode.OK;
import static org.apache.coyote.http11.response.StatusCode.UNAUTHORIZED;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.coyote.http11.common.FileReader;
import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.exception.LoginException;
import org.apache.coyote.http11.exception.MethodNotAllowedException;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class LoginController extends HttpController {
    public static final String LOGIN = "/login";
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    private static final String LOGIN_HTML = "/login.html";
    private static final String UNAUTHORIZED_HTML = "/401.html";
    private static final String JSESSIONID = "JSESSIONID";

    @Override
    public Response service(final Request request)  throws IOException, MethodNotAllowedException {
        final HttpMethod httpMethod = request.getHttpMethod();

        if (httpMethod.equals(GET)) {
            return doGet(request);
        }

        if (httpMethod.equals(POST)) {
            return doPost(request);
        }

        throw new MethodNotAllowedException(List.of(GET, POST));
    }

    @Override
    protected Response doPost(final Request request) throws IOException {
        final Map<String, String> bodies = request.getBody().getBodies();
        final String account = bodies.get("account");
        final String password = bodies.get("password");
        return login(account, password, request);
    }

    private Response login(final String account, final String password, final Request request) throws IOException {
        try {
            final User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new LoginException("로그인에 실패했습니다."));

            if (user.checkPassword(password)) {
                return successLogin(user, request.getSession());
            }
            throw new LoginException("로그인에 실패했습니다.");
        } catch (LoginException exception) {
            final String responseBody = FileReader.read(UNAUTHORIZED_HTML);
            final ContentType contentType = ContentType.findByName(UNAUTHORIZED_HTML);
            return Response.of(request.getHttpVersion(), UNAUTHORIZED, contentType,
                    responseBody);
        }
    }

    private Response successLogin(final User user, final Session session) {
        LOGGER.info("user {}", user);
        final Response response = Response.generateRedirectResponse(INDEX);
        session.put("user", user);
        response.setCookie(String.join(KEY_VALUE_DELIMITER, JSESSIONID, session.getId()));
        return response;
    }

    @Override
    protected Response doGet(final Request request) throws IOException {
        if (request.hasUserInSession()) {
            return Response.generateRedirectResponse(INDEX);
        }
        final String responseBody = FileReader.read(LOGIN_HTML);
        final ContentType contentType = ContentType.findByName(LOGIN_HTML);
        return Response.of(request.getHttpVersion(), OK, contentType, responseBody);
    }
}
