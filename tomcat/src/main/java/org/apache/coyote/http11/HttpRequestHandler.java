package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestHandler {

    private static final String USER_SESSION_INFO_NAME = "user";
    private static final Logger log = LoggerFactory.getLogger(HttpRequestHandler.class);

    public HttpResponse handle(RequestLine line, String responseBody, HttpCookies cookies) {
        if (line.getMethod().equals("GET") && line.getUrl().equals("/")) {
            return rootPage();
        }
        if (line.getMethod().equals("GET") && line.getUrl().startsWith("/login")) {
            return loginPage(line.getUrl(), cookies);
        }
        if (line.getMethod().equals("POST") && line.getUrl().startsWith("/register")) {
            return register(responseBody);
        }
        return staticPage(line.getUrl());
    }

    public HttpResponse handle(RequestLine line, String responseBody) {
        return handle(line, responseBody, new HttpCookies());
    }

    public HttpResponse handle(RequestLine line) {
        return handle(line, "", new HttpCookies());
    }

    private HttpResponse register(String body) {
        QueryParam param = new QueryParam(body);
        User newAccount = new User(param.getValue("account"), param.getValue("password"), param.getValue("email"));
        log.info("user : {}", newAccount);
        InMemoryUserRepository.save(newAccount);
        return HttpResponse.builder()
                .statusCode(HttpStatusCode.FOUND)
                .createSession(USER_SESSION_INFO_NAME, newAccount)
                .redirect("index.html");
    }

    private HttpResponse loginPage(String url, HttpCookies cookies) {
        String sessionId = cookies.getSessionId();
        if (!sessionId.isEmpty()) {
            Session session = SessionManager.getInstance().findSessionById(sessionId);
            User user = (User) session.findValue(USER_SESSION_INFO_NAME);
            log.info("session user : {}", user);
            return HttpResponse.builder()
                    .statusCode(HttpStatusCode.FOUND)
                    .staticResource("/index.html");
        }

        QueryParam queryParam = new QueryParam(parseQueryString(url));
        if (queryParam.getValue("password").isEmpty()) {
            return HttpResponse.builder()
                    .statusCode(HttpStatusCode.OK)
                    .staticResource("/login.html");
        }

        User account = InMemoryUserRepository.findByAccount(queryParam.getValue("account"))
                .orElseThrow(() -> new RuntimeException("계정 정보가 존재하지 않습니다."));

        if (account.checkPassword(queryParam.getValue("password"))) {
            return HttpResponse.builder()
                    .statusCode(HttpStatusCode.FOUND)
                    .createSession(USER_SESSION_INFO_NAME, account)
                    .redirect("index.html");
        }

        return HttpResponse.builder()
                .statusCode(HttpStatusCode.UNAUTHORIZED)
                .staticResource("/401.html");
    }

    private String parseQueryString(String uri) {
        int index = uri.indexOf("?");
        return uri.substring(index + 1);
    }

    private HttpResponse rootPage() {
        return HttpResponse.builder()
                .statusCode(HttpStatusCode.OK)
                .responseBody("Hello world!");
    }

    private HttpResponse staticPage(String url) {
        return HttpResponse.builder()
                .statusCode(HttpStatusCode.OK)
                .staticResource(url);
    }
}
