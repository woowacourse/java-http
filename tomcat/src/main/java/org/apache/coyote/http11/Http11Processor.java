package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.HeaderName;
import org.apache.catalina.HttpCookie;
import org.apache.catalina.HttpMethod;
import org.apache.catalina.HttpRequest;
import org.apache.catalina.Session;
import org.apache.catalina.manager.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    public static final String STATIC_PATH = "/static";

    private final Socket connection;
    private final SessionManager sessionManager;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.sessionManager = new SessionManager();
    }

    @Override
    public void run() {
//        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = new HttpRequest(inputStream);

            Path path = Path.of("");
            StringBuilder responseBody = new StringBuilder();
            String contentType = "";
            String statusCode = "";
            String location = "";
            HttpCookie cookie = new HttpCookie(httpRequest.get(HeaderName.COOKIE));

            if (httpRequest.isMethod(HttpMethod.GET)) {  // TODO: 404 추가하기
                String uri = httpRequest.getPath();
                if (uri.equals("/")) {
                    contentType = "text/html; charset=utf-8 ";
                    statusCode = "200 OK";
                    path = Path.of(getClass().getResource(STATIC_PATH + "/index.html").getPath());
                }
                if (uri.startsWith("/login")) {
                    contentType = "text/html; charset=utf-8 ";
                    // session이 있는 경우 다른 설정은 하지 않고, 쿠키에 그 세션 아이디를 넣어주고 리다이렉션한다.
                    if (httpRequest.hasCookie()&& cookie.hasESSIONID() && sessionManager.isSessionExist(cookie.getJESSIONID())) { // TODO: 객체에게 옮기기
                        statusCode = "302 FOUND ";
                        location = "/index.html";
                    }
                    if (!httpRequest.hasCookie() || !cookie.hasESSIONID() || !sessionManager.isSessionExist(cookie.getJESSIONID())) {
                        if (httpRequest.hasQueryParam()) { // 쿼리 파라미터가 없는 경우
                            statusCode = "200 OK";
                            path = Path.of(getClass().getResource(STATIC_PATH + "/login.html").getPath());
                        }
                        if (!httpRequest.hasQueryParam()) { // 쿼리 파라미터가 있는 경우
                            String account = httpRequest.getQueryParam("account");
                            String password = httpRequest.getQueryParam("password");
                            Optional<User> user = InMemoryUserRepository.findByAccount(account);
                            if (!user.isPresent()
                                || (user.isPresent() && !user.get().checkPassword(password))) {
                                statusCode = "401 UNAUTHORIZED ";
                                path = Path.of(getClass().getResource(STATIC_PATH + "/401.html").getPath());
                            }
                            if (user.isPresent() && user.get().checkPassword(password)) {
                                statusCode = "302 FOUND ";
                                location = "/index.html";

                                cookie.setJSESSIONID();
                                Session session = new Session(cookie.getJESSIONID());
                                session.setAttribute("user", user.get());
                                sessionManager.add(new Session(cookie.getJESSIONID()));
                            }
                        }
                    }
                }
                if (uri.startsWith("/register")) {
                    contentType = "text/html; charset=utf-8 ";
                    statusCode = "200 OK";
                    path = Path.of(getClass().getResource(STATIC_PATH + "/register.html").getPath());
                }
                if (uri.endsWith(".html")) {
                    contentType = "text/html; charset=utf-8 ";
                    statusCode = "200 OK";
                    path = Path.of(getClass().getResource(STATIC_PATH + uri).getPath());
                }
                if (uri.endsWith(".css")) {
                    contentType = "text/css; charset=utf-8 ";
                    statusCode = "200 OK";
                    path = Path.of(getClass().getResource(STATIC_PATH + uri).getPath());
                }
                if (uri.endsWith(".js")) {
                    contentType = "application/javascript ";
                    statusCode = "200 OK";
                    path = Path.of(getClass().getResource(STATIC_PATH + uri).getPath());
                }
            }

            if (httpRequest.isMethod(HttpMethod.POST)) {
                String requestBody = httpRequest.getBody();

                Map<String, String> userInfo = new HashMap<>();
                String[] body = requestBody.split("&");
                for (int i = 0; i < body.length; i++) {
                    String[] info = body[i].split("=");
                    userInfo.put(info[0], info[1]);
                }
                InMemoryUserRepository.save(new User(
                        userInfo.get("account"), userInfo.get("password"), userInfo.get("email")
                ));

                statusCode = "302 FOUND ";
                location = "/index.html";
            }


            String cookieResponse = cookie.getResponse();
            var response = "";
            if (statusCode.startsWith("200") || statusCode.startsWith("401")) {
                Files.readAllLines(path)
                        .stream()
                        .forEach(line -> responseBody.append(line).append("\n"));

                response = String.join("\r\n",
                        "HTTP/1.1 " + statusCode,
                        "Set-Cookie: " + cookieResponse,
                        "Content-Type: " + contentType,
                        "Content-Length: " + responseBody.toString().getBytes().length + " ",
                        "",
                        responseBody);
            }
            if (statusCode.startsWith("302")) {
                response = String.join("\r\n",
                        "HTTP/1.1 " + statusCode,
                        "Set-Cookie: " + cookieResponse,
                        "Location: " + location);
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
