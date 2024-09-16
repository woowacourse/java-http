package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.Session;
import org.apache.coyote.http11.Http11Cookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.apache.coyote.http11.Http11Processor.log;
import static org.apache.coyote.http11.Http11Processor.sessionManager;

public class LoginController extends AbstractController {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        String method = request.getMethod();

        if (method.equalsIgnoreCase("GET")) {
            doGet(request, response);
            return;
        }
        if (method.equalsIgnoreCase("POST")) {
            doPost(request, response);
            return;
        }
        throw new IllegalArgumentException("Method not supported: " + method);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> cookies = request.getCookies();
        if (doGetToLoginUser(response, cookies)) {
            return;
        }
        doGetPage(response, "/login.html", HttpStatusCode.OK);
    }

    private boolean doGetToLoginUser(HttpResponse response, Map<String, String> cookies) throws IOException {
        if (cookies.containsKey("JSESSIONID") && sessionManager.findSession(cookies.get("JSESSIONID")) != null) {
            doGetPage(response, "/index.html", HttpStatusCode.FOUND);
            return true;
        }
        return false;
    }

    private void doGetPage(HttpResponse response, String path, HttpStatusCode ok) throws IOException {
        response.setPath(path);
        response.setFileType("html");
        response.setHttpStatusCode(ok);
        response.setResponseBody(readBodyFromPath(response.getPath()));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, List<String>> body = request.getBody();

        String account = findAccountFromBody(body);
        User user = findUserByAccount(account);
        validatePassword(body);

        if (doPostSuccessLogin(response, user, body)) {
            return;
        }
        doPostErrorPage(response);
    }

    private String findAccountFromBody(Map<String, List<String>> body) {
        return Optional.ofNullable(body.get("account"))
                .filter(list -> !list.isEmpty())
                .map(List::getFirst)
                .orElseThrow(() -> new IllegalArgumentException("account not found"));
    }

    private User findUserByAccount(String account) {
        return InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
    }

    private void validatePassword(Map<String, List<String>> body) {
        if (!body.containsKey("password")) {
            throw new IllegalArgumentException("password not found");
        }
    }
    
    private Http11Cookie createLoginSession(User user) {
        Session session = Session.createRandomSession();
        session.setAttribute("user", user);
        Http11Cookie http11Cookie = Http11Cookie.sessionCookie(session.getId());
        sessionManager.add(session);
        return http11Cookie;
    }

    private boolean doPostSuccessLogin(HttpResponse response, User user, Map<String, List<String>> body)
            throws IOException {
        if (user.checkPassword(body.get("password").getFirst())) {
            log.info("user : {}", user);
            Http11Cookie http11Cookie = createLoginSession(user);
            doPostSuccessLoginPage(response, http11Cookie);
            return true;
        }
        return false;
    }

    private void doPostSuccessLoginPage(HttpResponse response, Http11Cookie http11Cookie) throws IOException {
        response.setPath("/index.html");
        response.setFileType("html");
        response.setHttpStatusCode(HttpStatusCode.FOUND);
        response.setHttp11Cookie(http11Cookie);
        final var responseBody = readBodyFromPath(response.getPath());
        response.setResponseBody(responseBody);
    }

    private void doPostErrorPage(HttpResponse response) throws IOException {
        response.setPath("/401.html");
        response.setFileType("html");
        response.setHttpStatusCode(HttpStatusCode.FOUND);
        final var responseBody = readBodyFromPath(response.getPath());
        response.setResponseBody(responseBody);
    }

    private String readBodyFromPath(String path) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static" + path);
        if (resource == null) {
            throw new IllegalArgumentException("invalid " + path);
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
