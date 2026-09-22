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
            String cookieHeader = getCookieHeader(httpCookie, session);

            String response = "";
            if (requestMethod.equals("GET")) {
                response = getResponse(httpRequest, cookieHeader, session);
            }
            if (requestMethod.equals("POST")) {
                response = getPostResponse(httpRequest, cookieHeader, session);
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getCookieHeader(HttpCookie httpCookie, Session session) {
        if (session.getId().equals(httpCookie.getJSessionId())) {
            return "";
        }
        return "Set-Cookie: JSESSIONID=" + session.getId() + "\r\n";
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

    private String getPostResponse(HttpRequest httpRequest, String cookieHeader, Session session) {
        Map<String, String> queryParameters = getQuerySeparate(httpRequest.getRequestBody());

        if (httpRequest.getPath().equals("/login")) {
            String account = queryParameters.get("account");
            String password = queryParameters.get("password");
            Optional<User> foundUser = InMemoryUserRepository.findByAccount(account);

            if (foundUser.isEmpty() || !foundUser.get().checkPassword(password)) {
                return getRedirectResponse("/401.html", getContentType(httpRequest.getPath()), cookieHeader);
            }
            session.setAttribute("user", foundUser.get());
            return getRedirectResponse("/index.html", getContentType(httpRequest.getPath()), cookieHeader);
        }

        if (httpRequest.getPath().equals("/register")) {
            Optional<User> foundUser = InMemoryUserRepository.findByAccount(queryParameters.get("account"));
            if (foundUser.isPresent()) {
                log.info("회원가입 실패! 아이디 : {}", queryParameters.get("account"));
                return getRedirectResponse("/register.html", getContentType(httpRequest.getPath()), cookieHeader);
            }
            User user = new User(queryParameters.get("account"), queryParameters.get("password"),
                    queryParameters.get("email"));
            InMemoryUserRepository.save(user);
            session.setAttribute("user", user);
            return getRedirectResponse("/index.html", getContentType(httpRequest.getPath()), cookieHeader);
        }
        return getRedirectResponse("/404.html", getContentType(httpRequest.getPath()), cookieHeader);
    }

    private String getResponse(HttpRequest httpRequest, String cookieHeader, Session session) throws IOException {
        if (httpRequest.getPath().equals("/login")) {
            if (session.getAttribute("user") != null) {
                log.info("로그인 페이지 접근! 세션 아이디: {}", session.getId());
                return getRedirectResponse("/index.html", getContentType(httpRequest.getPath()),
                        cookieHeader);

            }
        }
        if (!httpRequest.getPath().equals("/")) {
            String paths = getStaticResource(httpRequest.getPath());
            if (paths != null) {
                return getOkResponse(getContentType(httpRequest.getPath()), paths,
                        cookieHeader);
            }
        }
        return getOkResponse(getContentType(httpRequest.getPath()), "Hello world!",
                cookieHeader);
    }

    private String getRedirectResponse(String location, String contentType, String cookieHeader) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + location + " ",
                contentType,
                "Content-Length: " + 0 + " ",
                cookieHeader,
                "");
    }

    private String getOkResponse(String contentType, String body, String cookieHeader) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                contentType,
                "Content-Length: " + body.getBytes().length + " ",
                cookieHeader,
                body);
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
            return "Content-Type: text/css;charset=utf-8 ";
        }
        if (requestUri.endsWith(".js")) {
            return "Content-Type: text/javascript;charset=utf-8 ";
        }
        return "Content-Type: text/html;charset=utf-8 ";
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
