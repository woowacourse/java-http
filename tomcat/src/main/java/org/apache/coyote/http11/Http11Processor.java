package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()
        ) {
            HttpRequest httpRequest = HttpRequestParser.parse(inputStream);
            final var requestMethod = httpRequest.getMethod().toUpperCase();
            log.info("요청 메소드: {}, 요청 URI: {}", requestMethod, httpRequest.getPath());
            HttpCookie httpCookie = new HttpCookie(httpRequest.getHeaders().get("Cookie"));
            String sessionId = httpCookie.getJSessionId();
            Session session = getSession(sessionId);
            String sessionCookie = getSessionCookie(httpCookie, session);

            HttpResponse response = new HttpResponse(httpRequest.getVersion(), 404, "Not Found", "");
            if (requestMethod.equals("GET")) {
                response = getResponse(httpRequest, sessionCookie, session);
            }
            if (requestMethod.equals("POST")) {
                response = getPostResponse(httpRequest, sessionCookie, session);
            }

            outputStream.write(response.toBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getSessionCookie(HttpCookie httpCookie, Session session) {
        if (session.getId().equals(httpCookie.getJSessionId())) {
            return "";
        }
        return "JSESSIONID=" + session.getId();
    }

    private Session getSession(String sessionId) {
        Session session = SessionManager.findSession(sessionId);

        if (session == null) {
            String newSessionId = UUID.randomUUID().toString();
            session = new Session(newSessionId);
            SessionManager.add(session);
        }
        return session;
    }

    private HttpResponse getPostResponse(HttpRequest httpRequest, String sessionCookie, Session session) {
        Map<String, String> queryParameters = getQuerySeparate(httpRequest.getRequestBody());

        if (httpRequest.getPath().equals("/login")) {
            String account = queryParameters.get("account");
            String password = queryParameters.get("password");
            Optional<User> foundUser = InMemoryUserRepository.findByAccount(account);

            if (foundUser.isEmpty() || !foundUser.get().checkPassword(password)) {
                return getRedirectResponse(httpRequest, "/401.html", getContentType(httpRequest.getPath()),
                        sessionCookie);
            }
            session.setAttribute("user", foundUser.get());
            return getRedirectResponse(httpRequest, "/index.html", getContentType(httpRequest.getPath()),
                    sessionCookie);
        }

        if (httpRequest.getPath().equals("/register")) {
            Optional<User> foundUser = InMemoryUserRepository.findByAccount(queryParameters.get("account"));
            if (foundUser.isPresent()) {
                log.info("회원가입 실패! 아이디 : {}", queryParameters.get("account"));
                return getRedirectResponse(httpRequest, "/register.html", getContentType(httpRequest.getPath()),
                        sessionCookie);
            }
            User user = new User(queryParameters.get("account"), queryParameters.get("password"),
                    queryParameters.get("email"));
            InMemoryUserRepository.save(user);
            session.setAttribute("user", user);
            return getRedirectResponse(httpRequest, "/index.html", getContentType(httpRequest.getPath()),
                    sessionCookie);
        }
        return getRedirectResponse(httpRequest, "/404.html", getContentType(httpRequest.getPath()), sessionCookie);
    }

    private HttpResponse getResponse(HttpRequest httpRequest, String sessionCookie, Session session)
            throws IOException {
        if (httpRequest.getPath().equals("/login")) {
            if (session.getAttribute("user") != null) {
                log.info("로그인 페이지 접근! 세션 아이디: {}", session.getId());
                return getRedirectResponse(httpRequest, "/index.html", getContentType(httpRequest.getPath()),
                        sessionCookie);

            }
        }
        if (!httpRequest.getPath().equals("/")) {
            String paths = getStaticResource(httpRequest.getPath());
            if (paths != null) {
                return getOkResponse(httpRequest, getContentType(httpRequest.getPath()), paths, sessionCookie);
            }
        }
        return getOkResponse(httpRequest, getContentType(httpRequest.getPath()), "Hello world!", sessionCookie);
    }

    private HttpResponse getRedirectResponse(HttpRequest httpRequest, String location,
                                             String contentType, String sessionCookie) {
        HttpResponse response = new HttpResponse(httpRequest.getVersion(), 302, "Found", "");
        response.addHeader("Location", location + " ");
        response.addHeader("Content-Type", contentType);
        response.addHeader("Content-Length", "0 ");
        addSessionCookie(response, sessionCookie);
        return response;
    }

    private HttpResponse getOkResponse(HttpRequest httpRequest, String contentType, String body,
                                       String sessionCookie) {
        HttpResponse response = new HttpResponse(httpRequest.getVersion(), 200, "OK", body);
        response.addHeader("Content-Type", contentType);
        response.addHeader("Content-Length", body.getBytes(StandardCharsets.UTF_8).length + " ");
        addSessionCookie(response, sessionCookie);
        return response;
    }

    private void addSessionCookie(HttpResponse response, String sessionCookie) {
        if (!sessionCookie.isEmpty()) {
            response.addHeader("Set-Cookie", sessionCookie);
        }
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

    private String getContentType(String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css;charset=utf-8 ";
        }
        if (requestUri.endsWith(".js")) {
            return "text/javascript;charset=utf-8 ";
        }
        return "text/html;charset=utf-8 ";
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
