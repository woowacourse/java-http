package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        super.service(request, response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> queryParameters = getQuerySeparate(request.getRequestBody());

        String account = queryParameters.get("account");
        String password = queryParameters.get("password");
        Optional<User> foundUser = InMemoryUserRepository.findByAccount(account);

        if (foundUser.isEmpty() || !foundUser.get().checkPassword(password)) {
            getRedirectResponse(request, response, "/401.html", getContentType(request.getPath()),
                    "");
        }
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", foundUser.get());
        SessionManager.add(session);
        getRedirectResponse(request, response, "/index.html", getContentType(request.getPath()),
                "JSESSIONID=" + session.getId());

    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String resource = getStaticResource(request.getPath());
        HttpCookie httpCookie = new HttpCookie(request.getHeaders().get("Cookie"));
        Session session = SessionManager.findSession(httpCookie.getJSessionId());
        if (session != null) {
            log.info("로그인 페이지 접근! 세션 아이디: {}", session.getId());
            getRedirectResponse(request, response, "/index.html", getContentType(request.getPath()),
                    "");
            return;
        }
        getOkResponse(request, response, resource);
    }

    private String getContentType(String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css;charset=utf-8 ";
        }
        if (requestUri.endsWith(".js")) {
            return "text/javascript;charset=utf-8 ";
        }
        return "text/html;charset=utf-8 ";
    }

    private Map<String, String> getQuerySeparate(String requestUri) {
        Map<String, String> queryMap = new HashMap<>();
        int index = requestUri.indexOf("?");
        String queryString = requestUri.substring(index + 1);
        String[] queryParameters = queryString.split("&");
        for (String parameter : queryParameters) {
            String[] queryParameter = parameter.split("=", -1);
            queryMap.put(queryParameter[0], queryParameter[1]);
        }
        return queryMap;
    }

    private void addSessionCookie(HttpResponse response, String sessionCookie) {
        if (!sessionCookie.isEmpty()) {
            response.addHeader("Set-Cookie", sessionCookie);
        }
    }

    private void getRedirectResponse(HttpRequest httpRequest, HttpResponse httpResponse, String location,
                                     String contentType, String sessionCookie) {
        httpResponse.setVersion(httpRequest.getVersion());
        httpResponse.setStatusCode(302);
        httpResponse.setReasonPhrase("Found");
        httpResponse.addHeader("Location", location);
        httpResponse.addHeader("Content-Type", contentType);
        httpResponse.addHeader("Content-Length",
                String.valueOf(httpResponse.getResponseBody().getBytes().length) + " ");
        addSessionCookie(httpResponse, sessionCookie);
    }

    private void getOkResponse(HttpRequest httpRequest, HttpResponse httpResponse, String responseBody) {
        httpResponse.setVersion(httpRequest.getVersion());
        httpResponse.setStatusCode(200);
        httpResponse.setReasonPhrase("OK");
        httpResponse.setResponseBody(responseBody);
        httpResponse.addHeader("Content-Type", getContentType(httpRequest.getPath()));
        httpResponse.addHeader("Content-Length", responseBody.getBytes(StandardCharsets.UTF_8).length + " ");
    }

    @Nullable
    private String getStaticResource(String requestUri) throws IOException {
        URL url = getClass().getClassLoader().getResource("static" + requestUri);
        if (url == null) {
            requestUri = requestUri + ".html";
            url = getClass().getClassLoader().getResource("static" + requestUri);
        }
        if (url != null) {
            try (InputStream inputStream = url.openStream()) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
        return null;
    }
}


