package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import org.apache.catalina.session.HttpSession;
import org.apache.catalina.session.HttpSessionManger;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final HttpSessionManger HTTP_SESSION_MANGER = new HttpSessionManger();
    private static final String RESOURCE_PATH = "static";
    private static final String JSESSIONID = "JSESSIONID";

    private final Socket connection;

    public Http11Processor(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()
        ) {
            HttpRequest request = convertRequest(inputStream);
            HttpResponse response = dispatch(request);

            outputStream.write(response.toHttpResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest convertRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        List<String> header = new ArrayList<>();
        int contentLength = 0;
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            if (line.startsWith("Content-Length")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
            header.add(line);
        }

        char[] body = new char[contentLength];
        bufferedReader.read(body, 0, contentLength);

        return HttpRequest.of(header, new String(body));
    }

    private HttpResponse dispatch(HttpRequest request) throws IOException {
        HttpMethod method = request.getMethod();
        String requestUrl = request.getRequestUrl();

        HttpHeader responseHeader = new HttpHeader();

        if (requestUrl.equals("/") && method.isGet()) {
            String responseBody = "Hello world!";
            responseHeader.addHeader("Content-Type", "text/html;charset=utf-8");
            responseHeader.addHeader("Content-Length", String.valueOf(responseBody.getBytes().length));
            return new HttpResponse(responseHeader, HttpStatus.OK, responseBody);
        }

        if (requestUrl.startsWith("/login") && method.isPost()) {
            return login(request.getBody(), request.getCookie(), responseHeader);
        }

        if (requestUrl.startsWith("/login") && method.isGet()) {
            return getLoginPage(request.getCookie(), responseHeader);
        }

        if (requestUrl.startsWith("/register") && method.isPost()) {
            return register(request.getBody(), responseHeader);
        }

        String responseBody = readResource(requestUrl, responseHeader);
        return new HttpResponse(responseHeader, HttpStatus.OK, responseBody);
    }

    private String readResource(String requestUrl, HttpHeader responseHeader) throws IOException {
        String path = requestUrl.split("\\?")[0];
        if (!path.contains(".")) {
            path += ".html";
        }

        Path resourcePath = getResourcePath(path);
        String body = Files.readString(resourcePath);
        String contentType = Files.probeContentType(resourcePath);

        responseHeader.addHeader("Content-Type", contentType + ";charset=utf-8");
        responseHeader.addHeader("Content-Length", String.valueOf(body.getBytes().length));

        return body;
    }

    private Path getResourcePath(String path) {
        ClassLoader classLoader = getClass().getClassLoader();

        URL resourceUrl = classLoader.getResource(RESOURCE_PATH + path);
        if (resourceUrl == null) {
            resourceUrl = classLoader.getResource(RESOURCE_PATH + "/404.html");
        }

        return Path.of(resourceUrl.getPath());
    }

    private HttpResponse getLoginPage(HttpCookie httpCookie, HttpHeader responseHeader) throws IOException {
        if (httpCookie.isContains(JSESSIONID)) {
            responseHeader.addHeader("Location", "/index.html");
            return new HttpResponse(responseHeader, HttpStatus.FOUND);
        }

        String responseBody = readResource("/login.html", responseHeader);
        return new HttpResponse(responseHeader, HttpStatus.OK, responseBody);
    }

    private HttpResponse login(Map<String, String> body, HttpCookie cookie, HttpHeader responseHeader) {
        String account = body.get("account");
        String password = body.get("password");

        InMemoryUserRepository.findByAccount(account)
                .ifPresentOrElse(
                        login(cookie, responseHeader, password),
                        () -> responseHeader.addHeader("Location", "/401.html")
                );

        return new HttpResponse(responseHeader, HttpStatus.FOUND);
    }

    private Consumer<User> login(HttpCookie cookie, HttpHeader responseHeader, String password) {
        return user -> {
            if (!user.checkPassword(password)) {
                responseHeader.addHeader("Location", "/401.html");
            }

            if (!cookie.isContains(JSESSIONID)) {
                saveSession(cookie, responseHeader, user);
            }

            log.info("로그인 성공 :: account = {}", user.getAccount());
            responseHeader.addHeader("Location", "/index.html");
        };
    }

    private void saveSession(HttpCookie cookie, HttpHeader responseHeader, User user) {
        String sessionId = String.valueOf(UUID.randomUUID());
        cookie.addCookie(JSESSIONID, sessionId);
        responseHeader.addHeader("Set-Cookie", JSESSIONID + "=" + sessionId);

        HttpSession httpSession = new HttpSession(sessionId);
        httpSession.setAttribute("user", user);
        HTTP_SESSION_MANGER.add(httpSession);
    }

    private HttpResponse register(Map<String, String> body, HttpHeader responseHeader) {
        String account = body.get("account");
        String password = body.get("password");
        String email = body.get("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        log.info("회원 가입 성공 :: account = {}", user.getAccount());
        responseHeader.addHeader("Location", "/index.html");
        return new HttpResponse(responseHeader, HttpStatus.FOUND);
    }
}
