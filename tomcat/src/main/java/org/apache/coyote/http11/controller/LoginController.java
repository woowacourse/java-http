package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.HttpVersion;
import org.apache.coyote.http11.ReasonPhrase;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.apache.util.FileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String CONTENT_TYPE_TEXT_HTML = "text/html;charset=utf-8";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String PARAM_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String USER = "user";
    private static final String SET_COOKIE = "Set-Cookie";

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        log.info("로그인 요청");
        HttpCookie httpCookie = new HttpCookie(request.getHttpHeaders().get("Cookie"));
        final Map<String, String> queryParams = parseQueryParam(request.getHttpBody().getValue());

        final String account = queryParams.get("account");
        final String password = queryParams.get("password");

        if (account != null && password != null) {
            final Optional<User> loginUser = InMemoryUserRepository.findByAccount(account)
                    .filter(user -> user.checkPassword(password));

            if (loginUser.isPresent()) {
                final User user = loginUser.get();
                log.info("로그인 성공! 아이디 : {}", user.getAccount());

                if (httpCookie.get(JSESSIONID) != null) {
                    // 기존에 세션이 존재한다면, 세션을 삭제한다.
                    HttpSession existedSession = SessionManager.getInstance()
                            .findSession(httpCookie.get(JSESSIONID));
                    if (existedSession != null) {
                        SessionManager.getInstance().remove(existedSession);
                    }
                }

                final Session session = new Session(UUID.randomUUID().toString());
                session.setAttribute(USER, user);
                SessionManager.getInstance().add(session);

                response.setResponseLine(HttpVersion.HTTP_1_1, HttpStatusCode.HTTP_STATUS_302,
                        new ReasonPhrase("Found"));

                response.putHeader(SET_COOKIE, JSESSIONID + "=" + session.getId());
                response.putHeader(LOCATION, "/index.html");
                response.putHeader(CONTENT_LENGTH, "0");
                response.write();
                return;
            }

            response.setResponseLine(HttpVersion.HTTP_1_1, HttpStatusCode.HTTP_STATUS_302,
                    new ReasonPhrase("Found"));

            response.putHeader(LOCATION, "/401.html");
            response.putHeader(CONTENT_LENGTH, "0");

            response.write();
        }
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        log.info("로그인 페이지 접속");
        HttpCookie httpCookie = new HttpCookie(request.getHttpHeaders().get("Cookie"));

        if (httpCookie.get(JSESSIONID) != null) {
            final HttpSession session = SessionManager.getInstance().findSession(httpCookie.get(JSESSIONID));

            if (session != null) {
                response.setResponseLine(HttpVersion.HTTP_1_1, HttpStatusCode.HTTP_STATUS_302,
                        new ReasonPhrase("Found"));

                response.putHeader(LOCATION, "/index.html");

                response.write();
                return;
            }
        }

        response.setResponseLine(HttpVersion.HTTP_1_1, HttpStatusCode.HTTP_STATUS_200, new ReasonPhrase("OK"));

        FileReader fileReader = new FileReader();
        final String body = fileReader.readFile("static/login.html");

        response.putHeader(CONTENT_TYPE, CONTENT_TYPE_TEXT_HTML);
        response.setHttpBody(new HttpBody(body));

        response.write();
    }


    private Map<String, String> parseQueryParam(String queryLine) {
        final String[] params = queryLine.split(PARAM_DELIMITER);

        final Map<String, String> queries = new HashMap<>();
        for (String param : params) {
            final String[] keyToken = param.split(KEY_VALUE_DELIMITER);
            queries.put(URLDecoder.decode(keyToken[0], StandardCharsets.UTF_8),
                    URLDecoder.decode(keyToken[1], StandardCharsets.UTF_8));
        }
        return queries;
    }
}
