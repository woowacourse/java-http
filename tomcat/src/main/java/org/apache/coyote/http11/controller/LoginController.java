package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpException;
import org.apache.coyote.http11.SessionIdGenerator;
import org.apache.coyote.http11.StaticResourceResolver;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {
    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String JSESSIONID = "JSESSIONID";

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final SessionManager sessionManager;
    private final SessionIdGenerator sessionIdGenerator;

    public LoginController(SessionManager sessionManager, SessionIdGenerator sessionIdGenerator) {
        this.sessionManager = sessionManager;
        this.sessionIdGenerator = sessionIdGenerator;
    }

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException, HttpException {
        if (hasValidSession(request.requestHeader().cookie())) {
            return HttpResponse.found()
                    .location(INDEX_PAGE);
        }

        return firstVisit(request);
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        if (request.requestHeader().getContentLength().isEmpty()) {
            return HttpResponse.status(HttpStatus.LENGTH_REQUIRED);
        }

        return loginUser(request.requestBody());
    }

    private HttpResponse firstVisit(HttpRequest request) throws IOException, HttpException {
        String contentType = request.requestHeader().resolveContentType();
        URL url = StaticResourceResolver.findStaticResource(request.startLine().path(), contentType);

        if (url == null) {
            return HttpResponse.status(HttpStatus.NOT_FOUND);
        }

        Path path = new File(url.getFile()).toPath();
        String responseBody = Files.readString(path);
        return HttpResponse.ok()
                .contentType(contentType)
                .body(responseBody);
    }

    private boolean hasValidSession(HttpCookie cookie) {
        if (!cookie.hasJSessionId()) {
            return false;
        }
        return sessionManager.hasUser(cookie.getJSessionId());
    }

    private HttpResponse loginUser(HttpRequestBody body) {
        Map<String, String> formData = body.formData();

        String account = formData.get("account");
        String password = formData.get("password");

        if (account.isEmpty() || password.isEmpty()) {
            return HttpResponse.status(HttpStatus.BAD_REQUEST);
        }

        User user = InMemoryUserRepository.findByAccount(account)
                .orElse(null);

        if (user == null || !user.checkPassword(password)) {
            return HttpResponse.found().location(UNAUTHORIZED_PAGE);
        }

        log.info("user : {}", user);

        String sessionId = sessionIdGenerator.generate();
        sessionManager.add(new Session(sessionId, "user", user));
        return HttpResponse.found()
                .location(INDEX_PAGE)
                .setCookie(JSESSIONID, sessionId);
    }
}
