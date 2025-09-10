package org.apache.coyote.controller.dynamic;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Cookie;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.resource.StaticResourceReader;
import org.apache.coyote.error.ErrorCode;
import org.apache.coyote.error.HttpException;
import org.apache.coyote.httpRequest.HttpRequest;
import org.apache.coyote.httpRequest.httpBody.HttpBody;
import org.apache.coyote.httpRequest.httpHeader.HttpHeader;
import org.apache.coyote.httpRequest.httpHeader.HttpMethod;
import org.apache.coyote.httpResponse.HttpResponse;
import org.apache.coyote.httpResponse.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final StaticResourceReader staticResourceReader = StaticResourceReader.getInstance();
    private static final SessionManager sessionManager = new SessionManager();

    @Override
    public void service(
            final HttpRequest request,
            final HttpResponse response
    ) throws IOException {
        final HttpHeader httpHeader = request.getHttpHeader();
        final HttpMethod httpMethod = httpHeader.getHttpMethod();
        if (httpMethod.equals(HttpMethod.GET)) {
            doGet(request, response);
            return;
        }
        if (httpMethod.equals(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }
        throw new HttpException(ErrorCode.NOT_ALLOW_METHOD);
    }

    private void doGet(
            final HttpRequest request,
            final HttpResponse response
    ) throws IOException {
        final HttpHeader httpHeader = request.getHttpHeader();
        final String jsessionid = httpHeader.getCookie("JSESSIONID");
        if (jsessionid != null && sessionManager.findSession(jsessionid) != null) {
            response.updateStatusLine("HTTP/1.1", StatusCode.FOUND);
            response.addHeader("Content-Length", "0");
            response.addHeader("Location", "/index.html");
            return;
        }
        printMemberLog(httpHeader);
        responseLoginHtml(response);
    }

    private void doPost(
            final HttpRequest request,
            final HttpResponse response
    ) {
        if (processLogin(request, response)) {
            response.updateStatusLine("HTTP/1.1", StatusCode.FOUND);
            response.addHeader("Content-Length", "0");
            response.addHeader("Location", "/index.html");
            return;
        }
        throw new HttpException(ErrorCode.NOT_EXISTS_MEMBER);
    }

    private void responseLoginHtml(final HttpResponse httpResponse) throws IOException {
        final String body = staticResourceReader.getStaticResponseBody("static/login.html");
        httpResponse.updateStatusLine("HTTP/1.1", StatusCode.OK);
        httpResponse.updateBody(body);
        httpResponse.addHeader("Content-Type", "text/html;charset=utf-8");
        httpResponse.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
    }

    private void printMemberLog(final HttpHeader httpHeader) {
        final Map<String, String> queries = httpHeader.getQueries();
        final String account = queries.get("account");
        if (account == null) {
            return;
        }
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElse(null);
        if (user != null && user.checkPassword(queries.get("password"))) {
            log.info("user : {}", user);
        }
    }

    private boolean processLogin(
            final HttpRequest httpRequest,
            final HttpResponse httpResponse
    ) {
        final HttpBody httpBody = httpRequest.getHttpBody();
        final String account = httpBody.getData("account");
        final String password = httpBody.getData("password");
        if (account == null || password == null) {
            return false;
        }
        final User user = InMemoryUserRepository.findByAccount(account)
                .orElse(null);
        if (user != null && user.checkPassword(password)) {
            final String sessionId = UUID.randomUUID().toString();
            final Session session = new Session(sessionId);
            session.setAttribute("user", user);
            sessionManager.add(session);
            httpResponse.addCookie(new Cookie("JSESSIONID", sessionId));
            log.info("로그인 성공 user : {}", user);

            return true;
        }

        return false;
    }
}
