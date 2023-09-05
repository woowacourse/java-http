package nextstep.jwp.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.coyote.Controller;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

import static org.apache.coyote.http11.Http11Processor.sessionManager;
import static org.apache.coyote.http11.HttpHeaderType.*;
import static org.apache.coyote.http11.response.HttpStatusCode.*;

public class LoginController extends Controller {

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        final Map<String, Set<String>> requestType = new HashMap<>(Map.of(
                "/login", new HashSet<>(Set.of("GET", "POST"))
        ));

        if (requestType.containsKey(httpRequest.getTarget())) {
            final Set<String> methodType = requestType.get(httpRequest.getTarget());
            return methodType.contains(httpRequest.getMethod());
        }
        return false;
    }

    @Override
    public void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final HttpCookie httpCookie = httpRequest.getHeaders().getCookie();
        String sessionId = httpCookie.getCookie("JSESSIONID");
        final Session session = sessionManager.findSession(sessionId);
        if (session != null) { // already login user
            httpResponse.setStatusCode(FOUND);
            httpResponse.addHeader(CONTENT_TYPE, "text/html;charset=utf-8");
            httpResponse.addHeader(LOCATION, "/index.html");
        } else { // not login user
            final String resourceUrl = "login.html";
            String contentType = "text/html;charset=utf-8";

            final String acceptHeader = httpRequest.getHeaders().getHeaderValue(ACCEPT);
            if (acceptHeader != null) {
                contentType = acceptHeader.split(",")[0];
            }

            URL resource = getClass().getClassLoader().getResource("static/" + resourceUrl);
            if (resource != null) {
                httpResponse.setStatusCode(OK);
            } else {
                resource = getClass().getClassLoader().getResource("static/" + "404.html");
                httpResponse.setStatusCode(NOT_FOUND);
                contentType = "text/html;charset=utf-8";
            }

            final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            httpResponse.addHeader(CONTENT_TYPE, contentType);
            httpResponse.setBody(responseBody);
        }
    }

    @Override
    public void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final HttpCookie httpCookie = httpRequest.getHeaders().getCookie();
        String sessionId = httpCookie.getCookie("JSESSIONID");

        Optional<User> user = InMemoryUserRepository.findByAccount(httpRequest.getBody().get("account"));
        if (user.isEmpty() || !user.get().checkPassword(httpRequest.getBody().get("password"))) {
            // invalid user
            httpResponse.addHeader(CONTENT_TYPE, "text/html;charset=utf-8");
            httpResponse.addHeader(LOCATION, "/401.html");
            httpResponse.setStatusCode(FOUND);
            return;
        }

        // valid user
//        log.info("user: {}", user.get());

        if (sessionId != null) { // if already have session
            httpResponse.addHeader(CONTENT_TYPE, "text/html;charset=utf-8");
            httpResponse.addHeader(LOCATION, "/index.html");
            httpResponse.setStatusCode(FOUND);
            return;
        }

        // if no session
        Session session = new Session(String.valueOf(UUID.randomUUID()));
        session.setAttribute("user", user);
        sessionManager.add(session);
        sessionId = session.getId();

        httpResponse.addHeader(CONTENT_TYPE, "text/html;charset=utf-8");
        httpResponse.addHeader(LOCATION, "/index.html");
        httpResponse.addHeader(SET_COOKIE, "JSESSIONID=" + sessionId);
        httpResponse.setStatusCode(FOUND);
    }
}
