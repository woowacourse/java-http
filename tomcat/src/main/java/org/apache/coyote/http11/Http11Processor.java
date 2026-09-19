package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
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
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            String[] requestLineParts = bufferedReader.readLine().split(" ");
            final var requestMethod = requestLineParts[0].toUpperCase();
            log.info("요청 메소드: {}, 요청 URI: {}", requestMethod, requestLineParts[1]);
            Map<String, String> requestHeaders = readRequestHeaders(bufferedReader);
            HttpCookie httpCookie = new HttpCookie(requestHeaders.get("Cookie"));
            String cookieHeader = getCookieHeader(httpCookie);
            String sessionId = httpCookie.getJSessionId();
            Session session = getSession(sessionId);

            String response = "";
            if (requestMethod.equals("GET")) {
                response = getResponse(requestLineParts[1], cookieHeader, session);
            }
            if (requestMethod.equals("POST")) {
                response = getPostResponse(bufferedReader, requestLineParts[1], requestHeaders, cookieHeader);
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> readRequestHeaders(BufferedReader bufferedReader)
            throws IOException {
        Map<String, String> requestHeaders = new HashMap<>();
        String requestHeaderLine;
        while ((requestHeaderLine = bufferedReader.readLine()) != null) {
            if (requestHeaderLine.isEmpty()) {
                break;
            }
            String[] header = requestHeaderLine.split(":", 2);
            requestHeaders.put(header[0].trim(), header[1].trim());
        }
        return requestHeaders;
    }

    private String getCookieHeader(HttpCookie httpCookie) {
        if (httpCookie.hasJSessionId()) {
            log.info("기존 세션 확인: {}", httpCookie.getJSessionId());
            return "";
        }
        String sessionId = httpCookie.createJSessionId();
        log.info("세션 생성: {}", sessionId);
        return "Set-Cookie: JSESSIONID=" + sessionId + "\r\n";
    }

    private Session getSession(String sessionId) {
        Session session = SessionManager.findSession(sessionId);

        if (session == null) {
            session = new Session(sessionId);
            SessionManager.add(session);
        }
        return session;
    }

    private String getPostResponse(BufferedReader bufferedReader, String requestUri,
                                   Map<String, String> requestHeaders, String cookieHeader)
            throws IOException {
        int contentLength = Integer.parseInt(requestHeaders.getOrDefault("Content-Length", "0"));
        String requestBody = readRequestBody(bufferedReader, contentLength);
        Map<String, String> queryParameters = getQuerySeparate(requestBody);

        if (InMemoryUserRepository.findByAccount(queryParameters.get("account")).isEmpty()) {
            User user = new User(queryParameters.get("account"), queryParameters.get("password"),
                    queryParameters.get("email"));
            InMemoryUserRepository.save(user);
            return getRedirectResponse("/index.html", getContentType(requestUri), cookieHeader);
        }
        log.info("회원가입 실패! 아이디 : {}", queryParameters.get("account"));
        return getRedirectResponse("/register.html", getContentType(requestUri), cookieHeader);
    }

    @Nonnull
    private String readRequestBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private String getResponse(String requestUri, String cookieHeader, Session session) throws IOException {
        if (requestUri.contains("/login?")) {
            Map<String, String> queryMap = getQuerySeparate(requestUri);
            String account = queryMap.get("account");
            String password = queryMap.get("password");
            Optional<User> foundUser = InMemoryUserRepository.findByAccount(account);
            if (foundUser.isEmpty()) {
                return getRedirectResponse("/401.html", getContentType(requestUri), cookieHeader);
            }

            User user = foundUser.get();

            if (user.checkPassword(password)) {
                log.info("로그인 성공! 아이디: {}, 세션 아이디: {}", user.getAccount(), session.getId());
                session.setAttribute("user", user);
                return getRedirectResponse("/index.html", getContentType(requestUri), cookieHeader);
            }
            return getRedirectResponse("/401.html", getContentType(requestUri), cookieHeader);
        }
        if (requestUri.equals("/login")) {
            if (session.getAttribute("user") != null) {
                log.info("로그인 페이지 접근! 세션 아이디: {}", session.getId());
                return getRedirectResponse("/index.html", getContentType(requestUri), cookieHeader);

            }
        }

        if (!requestUri.equals("/")) {
            String paths = getStaticResource(requestUri);
            if (paths != null) {
                return getOkResponse(getContentType(requestUri), paths, cookieHeader);
            }
        }
        return getOkResponse(getContentType(requestUri), "Hello world!", cookieHeader);
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
