package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.Http11Cookie;
import org.apache.catalina.Session;
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
        if (cookies.containsKey("JSESSIONID") && sessionManager.findSession(cookies.get("JSESSIONID")) != null) {
            response.setPath("/index.html");
            response.setFileType("html");
            response.setHttpStatusCode(HttpStatusCode.FOUND);
            response.setResponseBody(readBodyFromPath(response.getPath()));
            return;
        }

        response.setPath("/index.html");
        response.setFileType("html");
        response.setHttpStatusCode(HttpStatusCode.OK);
        response.setResponseBody(readBodyFromPath(response.getPath()));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, List<String>> body = request.getBody();
        String account = Optional.ofNullable(body.get("account"))
                .filter(list -> !list.isEmpty())
                .map(List::getFirst)
                .orElseThrow(() -> new IllegalArgumentException("account not found"));

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        if (!body.containsKey("password")) {
            throw new IllegalArgumentException("password not found");
        }

        if (user.checkPassword(body.get("password").getFirst())) {
            log.info("user : {}", user);
            Session session = Session.createRandomSession();
            session.setAttribute("user", user);
            Http11Cookie http11Cookie = Http11Cookie.sessionCookie(session.getId());
            sessionManager.add(session);
            response.setPath("/index.html");
            response.setFileType("html");
            response.setHttpStatusCode(HttpStatusCode.FOUND);
            response.setHttp11Cookie(http11Cookie);
            final var responseBody = readBodyFromPath(response.getPath());
            response.setResponseBody(responseBody);
            return;
        }
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
