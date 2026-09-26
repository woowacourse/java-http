package org.apache.coyote.http11;


import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.HttpCookie;
import com.techcourse.model.HttpRequest;
import com.techcourse.model.HttpResponse;
import com.techcourse.model.StaticResource;
import com.techcourse.model.StaticResourceLoader;
import com.techcourse.model.User;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final Socket connection;
    private final SessionManager sessionManager = new SessionManager();
    private final StaticResourceLoader resourceLoader = new StaticResourceLoader();

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
             final var outputStream = connection.getOutputStream()) {

            HttpRequest request = HttpRequest.parse(inputStream);

            String requestMethod = request.getRequestLine().getMethod().toUpperCase();

            // 리소스 경로 찾기
            String requestPath = request.getRequestLine().getPath();

            // 헤더 읽기
            Map<String, List<String>> requestHeaders = request.getHeaders();

            // 바디 읽기
            String requestBody = new String(request.getBody(), StandardCharsets.UTF_8);
            // todo 바디 파싱
            Map<String, String> formParameters = parseFormParameters(requestBody);

            // 쿼리 파싱
            Map<String, List<String>> queryParameters = request.getRequestLine().getQueryParameters();

            HttpResponse httpResponse;
            if (requestPath.equals("/")) {
                httpResponse = HttpResponse.of(
                        "200",
                        "OK",
                        Map.of("Content-Type", List.of("text/html;charset=utf-8 ")),
                        "Hello world!".getBytes()
                );
            } else if (requestPath.startsWith("/login")) {
                httpResponse = handleLoginRequest(formParameters, requestHeaders, requestMethod);
            } else if (requestPath.startsWith("/register") && requestMethod.equals("POST")) {
                User user = new User(formParameters.get("account"), formParameters.get("password"),
                        formParameters.get("email"));
                InMemoryUserRepository.save(user);

                Session session = getSession(requestHeaders, true);
                session.setAttribute("user", user);
                HttpCookie cookie = new HttpCookie(session.getId());

                httpResponse = HttpResponse.of("302", "FOUND", Map.of("Location", List.of("/index.html"), "Set-Cookie", List.of(cookie.toString())), new byte[0]);
                log.info("회원가입 성공 : {}", user.toString());
            } else {
                httpResponse = createResourceResponse(requestPath);
            }

            httpResponse = addSessionCookieIfNeeded(
                    requestHeaders,
                    httpResponse
            );

            writeHttpResponse(outputStream, httpResponse);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpResponse addSessionCookieIfNeeded(Map<String, List<String>> requestHeaders, HttpResponse response) {

        // 이미 JSESSIONID 있으면 추가 발급 x
        if (getSessionId(requestHeaders) != null) {
            return response;
        }

        // 이미 위에서 세션을 응답에 넣어줄 경우 발급 X
        boolean hasSetCookie = response.getHeaders().containsKey("set-cookie");
        if (hasSetCookie) {
            return response;
        }

        Session session = Session.create();
        sessionManager.add(session);

        Map<String, List<String>> headers = new HashMap<>(response.getHeaders());

        headers.put(
                "Set-Cookie",
                List.of(new HttpCookie(session.getId()).toString())
        );

        return  HttpResponse.of(
                response.getStatusCode(),
                response.getStatusMessage(),
                headers,
                response.getBody()
        );
    }

    private String getSessionId(Map<String, List<String>> requestHeaders) {
        List<String> cookieHeaders = requestHeaders.get("cookie");

        if (cookieHeaders == null) {
            return null;
        }

        for (String cookieHeader : cookieHeaders) {
            for (String cookie : cookieHeader.split(";")) {
                String[] parts = cookie.trim().split("=", 2);

                if (parts.length == 2 && parts[0].equals("JSESSIONID")) {
                    return parts[1].trim();
                }
            }
        }

        return null;
    }

    private Session getSession(Map<String, List<String>> requestHeaders, boolean create) {
        String sessionId = getSessionId(requestHeaders);

        if (sessionId != null) {
            Session session = sessionManager.findSession(sessionId);

            if (session != null) {
                return session;
            }
        }

        if (!create) {
            return null;
        }

        Session session = Session.create();
        sessionManager.add(session);
        return session;
    }

    private Map<String, String> parseFormParameters(String requestBody) throws URISyntaxException, IOException {
        Map<String, String> partsMap = new HashMap<>();

        if (requestBody == null || requestBody.isBlank()) {
            return partsMap;
        }

        String[] parts = requestBody.split("&");
        for (String part : parts) {
            String[] keyValue = part.split("=", 2);
            if (keyValue.length != 2) {
                continue;
            }

            String key = keyValue[0].trim();
            String value = keyValue[1].trim();

            String encodedKey = URLDecoder.decode(key, "UTF-8");
            String encodedValue = URLDecoder.decode(value, "UTF-8");

            partsMap.put(encodedKey, encodedValue);
        }
        return partsMap;
    }


    private void writeHttpResponse(OutputStream outputStream, HttpResponse httpResponse) throws IOException {
        byte[] body = httpResponse.getBody();
        StringBuilder header = new StringBuilder();

        header.append("HTTP/1.1 ")
                .append(httpResponse.getStatusCode())
                .append(" ")
                .append(httpResponse.getStatusMessage())
                .append("\r\n");

        for (Map.Entry<String, List<String>> entry : httpResponse.getHeaders().entrySet()) {

            String headerName = entry.getKey();

            for (String value : entry.getValue()) {
                header.append(headerName)
                        .append(": ")
                        .append(value)
                        .append("\r\n");
            }
        }

        header.append("Content-Length: ")
                .append(body.length)
                .append(" ")
                .append("\r\n\r\n");

        outputStream.write(header.toString().getBytes());
        outputStream.write(body);

    }

    private HttpResponse handleLoginRequest(Map<String, String> formParameters,
                                            Map<String, List<String>> requestHeaders, String requestMethod)
            throws URISyntaxException, IOException {

        // 로그인 페이지 접근
        if (requestMethod.equals("GET")) {
            Session session = getSession(requestHeaders, false);

            if (session != null && session.getAttribute("user") != null) {
                return createRedirectResponse("/index.html");
            }

            return createResourceResponse("/login");
        }

        // 로그인 제출
        String account = formParameters.get("account");
        String password = formParameters.get("password");

        if (account == null || password == null) {
            return createRedirectResponse("/login.html");
        }

        final var user = InMemoryUserRepository
                .findByAccount(account)
                .filter(foundUser -> foundUser.checkPassword(password))
                .orElse(null);

        if (user == null) {
            return createRedirectResponse("/401.html");
        }

        // 성공한 경우에만 세션 생성
        Session session = getSession(requestHeaders, true);
        session.setAttribute("user", user);

        HttpCookie cookie = new HttpCookie(session.getId());

        return createLoginSuccessResponse(List.of(cookie.toString()));
    }

    private HttpResponse createResourceResponse(String path) throws IOException, URISyntaxException {
        StaticResource resource = resourceLoader.load(path);

        return HttpResponse.of(
                "200",
                "OK",
                Map.of("Content-Type", List.of(resource.contentType())),
                resource.body()
        );
    }

    private HttpResponse createRedirectResponse(String path) {
        return HttpResponse.redirect(
                "302",
                "FOUND",
                path
        );
    }

    private HttpResponse createLoginSuccessResponse(List<String> cookies) {
        return HttpResponse.of(
                "302",
                "FOUND",
                Map.of(
                        "Location", List.of("/index.html"),
                        "Set-Cookie", cookies
                ),
                new byte[0]
        );
    }
}
