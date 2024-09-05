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
import java.util.Optional;
import java.util.UUID;
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
            HttpRequest request = getRequest(inputStream);
            HttpMethod method = request.getMethod();
            String requestUrl = request.getRequestUrl();

            HttpHeader responseHeader = new HttpHeader();
            String response = getResponse(requestUrl, HttpStatus.OK, responseHeader);
            if (requestUrl.startsWith("/login") && method.isPost()) {
                response = login(request.getBody(), request.getCookie(), responseHeader);
            }

            if (requestUrl.startsWith("/login") && method.isGet()) {
                response = getLoginPage(request.getCookie(), responseHeader);
            }

            if (requestUrl.startsWith("/register") && method.isPost()) {
                response = register(request.getBody(), responseHeader);
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest getRequest(InputStream inputStream) throws IOException {
        List<String> request = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        int contentLength = 0;

        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            if (line.toLowerCase().startsWith("content-length")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
            request.add(line);
        }

        char[] body = new char[contentLength];
        bufferedReader.read(body, 0, contentLength);

        request.add(new String(body));
        return HttpRequest.from(request);
    }

    private String getResponse(String requestUrl, HttpStatus httpStatus, HttpHeader responseHeader) throws IOException {
        String responseBody = getResponseBody(requestUrl);

        String responseLine = "HTTP/1.1 " + httpStatus.toHttpHeader() + " ";

        responseHeader.addHeader("Content-Type", ContentType.findContentType(requestUrl) + ";charset=utf-8");
        responseHeader.addHeader("Content-Length", String.valueOf(responseBody.getBytes().length));

        if (httpStatus.isFound()) {
            responseHeader.addHeader("Location", requestUrl);
        }

        return String.join("\r\n", responseLine, responseHeader.toHttpHeader(), "", responseBody);
    }

    private String getResponseBody(String requestUrl) throws IOException {
        if (requestUrl.equals("/")) {
            return "Hello world!";
        }

        String path = requestUrl.split("\\?")[0];
        if (!path.contains(".")) {
            path += ".html";
        }

        URL resourceUrl = getResourceUrl(path);

        Path resourcePath = Path.of(resourceUrl.getPath());
        return Files.readString(resourcePath);
    }

    private URL getResourceUrl(String path) {
        URL resourceUrl = getClass().getClassLoader()
                .getResource(RESOURCE_PATH + path);

        if (resourceUrl == null) {
            return getClass().getClassLoader()
                    .getResource(RESOURCE_PATH + "/404.html");
        }

        return resourceUrl;
    }

    private String getLoginPage(HttpCookie httpCookie, HttpHeader responseHeader) throws IOException {
        if (httpCookie.isContains(JSESSIONID)) {
            return getResponse("/index.html", HttpStatus.FOUND, responseHeader);
        }

        return getResponse("/login.html", HttpStatus.OK, responseHeader);
    }

    private String login(Map<String, String> body, HttpCookie cookie, HttpHeader responseHeader) throws IOException {
        String account = body.get("account");
        String password = body.get("password");

        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isEmpty() || !user.get().checkPassword(password)) {
            return getResponse("/401.html", HttpStatus.FOUND, responseHeader);
        }

        if (!cookie.isContains(JSESSIONID)) {
            String sessionId = String.valueOf(UUID.randomUUID());
            cookie.addCookie(JSESSIONID, sessionId);
            responseHeader.addHeader("Set-Cookie", JSESSIONID + "=" + sessionId);

            HttpSession httpSession = new HttpSession(sessionId);
            httpSession.setAttribute("user", user.get());
            HTTP_SESSION_MANGER.add(httpSession);
        }

        log.info("로그인 성공 :: account = {}", user.get().getAccount());
        return getResponse("/index.html", HttpStatus.FOUND, responseHeader);
    }

    private String register(Map<String, String> body, HttpHeader responseHeader) throws IOException {
        User user = new User(body.get("account"), body.get("password"), body.get("email"));
        InMemoryUserRepository.save(user);

        log.info("회원 가입 성공 :: account = {}", user.getAccount());
        return getResponse("/index.html", HttpStatus.FOUND, responseHeader);
    }
}
