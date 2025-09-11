package org.apache.coyote.http11;

import static com.techcourse.exception.ErrorMessage.ACCOUNT_NOT_FOUND;
import static com.techcourse.exception.ErrorMessage.INVALID_PASSWORD;
import static com.techcourse.exception.ErrorMessage.INVALID_QUERY_STRING;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.ErrorMessage;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.coyote.CookieManager;
import org.apache.coyote.Processor;
import org.apache.coyote.Request;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    private Request request;

    private Response response;

    private SessionManager sessionManager;

    public Http11Processor(final Socket connection) {
        response.setProtocolVersion("HTTP/1.1");
        sessionManager = new SessionManager();
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
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        ) {
            request = new Request(br);
            response = new Response();

            String httpMethod = request.getHttpMethod();
            String uri = request.getUrl();
            Path path = parsePath(uri);
            // 로그인 처리
            if (uri.startsWith("/login")) {
                if (httpMethod.equals("GET")) {
                    if (request.containsCookieKey("JSESSIONID")) {
                        // 세션 아이디 로그인 후 리다이엑트
                        String jsessionid = request.getCookieValue("JSESSIONID");
                        Session session = sessionManager.findSession(jsessionid);
                        if (session != null) {
                            log.info(session.getUser().toString());
                            redirectToIndexPage(path, outputStream);
                            return;
                        }
                        sessionManager.remove(jsessionid);
                    }
                }
                if (httpMethod.equals("POST")) {
                    // 리퀘스트 바디 로그인
                    requestBodyLogin(path, outputStream);
                    return;
                }
                if (uri.contains("?")) {
                    // 쿼리 파라미터 로그인
                    queryParameterLogin(uri, path, outputStream);
                    return;
                }
            }
            // 회원 가입 처리
            if (httpMethod.equals("POST") && uri.startsWith("/register")) {
                if (register(parseQueryString(request.getBody()))) {
                    redirectToIndexPage(path, outputStream);
                    return;
                }
            }
            // 기타 정적 리소스 반환
            staticResourceResponse(path);
            sendResponse(outputStream);
        } catch (IOException | UncheckedServletException | URISyntaxException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void requestBodyLogin(Path path, OutputStream outputStream) throws IOException, URISyntaxException {
        if (login(parseQueryString(request.getBody()))) {
            redirectToIndexPage(path, outputStream);
            return;
        }
        throw new IllegalArgumentException(INVALID_PASSWORD.getMessage());
    }

    private void queryParameterLogin(String uri, Path path, OutputStream outputStream)
            throws IOException, URISyntaxException {
        if (login(parseQueryParameter(uri))) {
            redirectToIndexPage(path, outputStream);
            return;
        }
        throw new IllegalArgumentException(INVALID_PASSWORD.getMessage());
    }

    private void redirectToIndexPage(Path path, OutputStream outputStream) throws IOException, URISyntaxException {
        response.setHttpStatusCode(HttpStatusCode.FOUND);
        response.addHeader("Location", "/index.html");
        response.addHeader("Content-Type", getContentType(path));
        response.addHeader("Content-Length", response.getContentLength());
        sendResponse(outputStream);
    }

    private boolean register(Map<String, String> params) {
        String account = params.get("account");
        String password = params.get("password");
        String email = params.get("email");
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return InMemoryUserRepository.findByAccount(account).isPresent();
    }

    private Path parsePath(String uri) {
        int idx = uri.indexOf('?');
        if (idx == -1) {
            return Paths.get(uri);
        }
        return Paths.get(uri.substring(0, idx));
    }

    private Map<String, String> parseQueryParameter(String uri) {
        if (!uri.contains("?")) {
            throw new IllegalArgumentException(INVALID_QUERY_STRING.getMessage());
        }
        String queryString = uri.split("\\?")[1];
        return parseQueryString(queryString);
    }

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] parts = pair.split("=");
            if (parts.length == 2) {
                params.put(parts[0], parts[1]);
            }
        }
        return params;
    }

    private boolean login(Map<String, String> params) {
        String account = params.get("account");
        String password = params.get("password");
        if (account == null || password == null) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_LOGIN_REQUEST.getMessage());
        }
        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException(ACCOUNT_NOT_FOUND.getMessage()));
        user.logUserInfo(password, log);
        if (user.checkPassword(password)) {   // 로그인 성공
            String sessionId = UUID.randomUUID().toString();    // 세션 ID 생성
            Session session = new Session(sessionId);   // 새 세션 생성
            session.setAttribute("user", user); // 세션에 user 정보에 user 저장
            sessionManager.addSession(sessionId, session);  // 세션 맵에 새 세션 등록
            CookieManager cookieManager = new CookieManager();
            cookieManager.addCookie("JSESSIONID", sessionId);  // 쿠키에 세션 아이디 매핑
            for (Map.Entry<String, String> entry : cookieManager.getCookieMap().entrySet()) {
                response.addCookie(entry.getKey(), entry.getValue());
            }
        }
        return user.checkPassword(password);
    }

    private void sendResponse(OutputStream outputStream) throws IOException, URISyntaxException {
        String httpFormatResponse = formatHttpResponse();
        outputStream.write(httpFormatResponse.getBytes());
        outputStream.flush();
    }

    private void staticResourceResponse(Path path) throws IOException, URISyntaxException {
        response.setHttpStatusCode(HttpStatusCode.OK);
        response.addHeader("Content-Type", getContentType(path));
        response.setBody(getStaticResource(path));
        response.addHeader("Content-Length", response.getContentLength());
    }

    private String getContentType(Path path) throws IOException {
        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            contentType = "text/html";
        }
        return contentType + ";charset=utf-8";
    }

    private String getStaticResource(Path path) throws IOException, URISyntaxException {
        if (path.equals(Path.of("\\"))) {
            return "Hello world!";
        }
        Path staticPath = getStaticPath(path);
        return new String(Files.readAllBytes(staticPath));
    }

    private Path getStaticPath(Path path) throws URISyntaxException {
        if (!path.toString().contains(".")) {
            path = Path.of(path + ".html");
        }
        return Paths.get(getClass().getClassLoader().getResource("static" + path).toURI());
    }

    private String formatHttpResponse() {
        String ret = String.join("\r\n",
                response.getProtocolVersion() + " " +
                        response.getStatusCode() + " " +
                        response.getStatusMessage() + " ",
                response.getHeaders()
                        .entrySet()
                        .stream()
                        .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                        .collect(Collectors.joining("\r\n")));
        if (response.getCookieMapSize() != 0) {
            ret += "\r\n" + response.getCookieMap()
                    .entrySet()
                    .stream()
                    .map(entry -> "Set-Cookie: " + entry.getKey() + "=" + entry.getValue() + " ")
                    .collect(Collectors.joining("\r\n"));
        }
        ret += "\r\n\r\n" + response.getBody();
        return ret;
    }
}
