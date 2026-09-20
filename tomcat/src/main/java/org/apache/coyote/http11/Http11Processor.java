package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String CONTENT_TYPE_TEXT_HTML = "text/html;charset=utf-8";
    private static final String CONTENT_TYPE_TEXT_CSS = "text/css;charset=utf-8";
    private static final String CONTENT_TYPE_TEXT_JAVASCRIPT = "text/javascript;charset=utf-8";
    private static final String PARAM_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String COOKIE = "Cookie";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String USER = "user";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String SET_COOKIE = "Set-Cookie";

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
             final var inputStreamReader = new InputStreamReader(inputStream);
             final var bufferedReader = new BufferedReader(inputStreamReader);
             final var outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = new HttpRequest(bufferedReader);
            HttpPath httpUrl = httpRequest.getRequestLine().getHttpPath();
            HttpMethod httpMethod = httpRequest.getRequestLine().getHttpMethod();

            HttpHeaders httpHeaders = httpRequest.getHttpHeaders();
            String httpBody = httpRequest.getHttpBody().getValue();

            // Parse Cookie
            // 이 부분은 추후 Header 속성을 Object로 바꾸고, Map<HttpCookie> 를 가지게 하면 될 거 같음.
            final HttpCookie httpCookie = new HttpCookie(httpHeaders.getHeaders().get(COOKIE));

            HttpResponse httpResponse = new HttpResponse();

            log.info("{}, {}", httpUrl, httpMethod);
            if (httpUrl.startsWith("/login") && httpMethod == HttpMethod.GET) {
                log.info("로그인 페이지 접속");
                // 세션이 유효하면 index.html로 리다이렉트한다.
                if (httpCookie.get(JSESSIONID) != null) {
                    final HttpSession session = SessionManager.getInstance().findSession(httpCookie.get(JSESSIONID));

                    if (session != null) {
                        httpResponse.setResponseLine(HttpVersion.HTTP_1_1, HttpStatusCode.HTTP_STATUS_302,
                                new ReasonPhrase("Found"));

                        httpResponse.putHeader(LOCATION, "/index.html");
                        httpResponse.putHeader(CONTENT_LENGTH, "0");

                        httpResponse.sendTo(outputStream);
                        return;
                    }
                }

                httpResponse.setResponseLine(HttpVersion.HTTP_1_1, HttpStatusCode.HTTP_STATUS_200,
                        new ReasonPhrase("OK"));

                final String body = readFile("static/login.html");

                httpResponse.putHeader(CONTENT_TYPE, CONTENT_TYPE_TEXT_HTML);
                httpResponse.putHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
                httpResponse.setHttpBody(new HttpBody(body));

                httpResponse.sendTo(outputStream);
                return;
            }

            if (httpUrl.startsWith("/login") && httpMethod == HttpMethod.POST) {
                final Map<String, String> queryParams = parseQueryParam(httpBody);

                final String account = queryParams.get("account");
                final String password = queryParams.get("password");

                if (account != null && password != null) {
                    final Optional<User> loginUser = InMemoryUserRepository.findByAccount(account)
                            .filter(user -> user.checkPassword(password));

                    if (loginUser.isPresent()) {
                        final User user = loginUser.get();
                        log.info("로그인 성공! 아이디 : {}", user.getAccount());

                        if (httpCookie.get(JSESSIONID) != null) {
                            // 기존에 세션이 존재한다면, 세션을 삭제한다.
                            HttpSession existedSession = SessionManager.getInstance()
                                    .findSession(httpCookie.get(JSESSIONID));
                            if (existedSession != null) {
                                SessionManager.getInstance().remove(existedSession);
                            }
                        }

                        final Session session = new Session(UUID.randomUUID().toString());
                        session.setAttribute(USER, user);
                        SessionManager.getInstance().add(session);

                        httpResponse.setResponseLine(HttpVersion.HTTP_1_1, HttpStatusCode.HTTP_STATUS_302,
                                new ReasonPhrase("Found"));

                        httpResponse.putHeader(SET_COOKIE, JSESSIONID + "=" + session.getId());
                        httpResponse.putHeader(LOCATION, "/index.html");
                        httpResponse.putHeader(CONTENT_LENGTH, "0");
                        httpResponse.sendTo(outputStream);
                        return;
                    }

                    httpResponse.setResponseLine(HttpVersion.HTTP_1_1, HttpStatusCode.HTTP_STATUS_302,
                            new ReasonPhrase("Found"));

                    httpResponse.putHeader(LOCATION, "/401.html");
                    httpResponse.putHeader(CONTENT_LENGTH, "0");

                    httpResponse.sendTo(outputStream);
                    return;
                }
            }

            if (httpUrl.startsWith("/register") && httpMethod == HttpMethod.GET) {
                final String body = readFile("static/register.html");

                httpResponse.setResponseLine(HttpVersion.HTTP_1_1, HttpStatusCode.HTTP_STATUS_200,
                        new ReasonPhrase("OK"));
                httpResponse.putHeader(CONTENT_TYPE, CONTENT_TYPE_TEXT_HTML);
                httpResponse.putHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
                httpResponse.setHttpBody(new HttpBody(body));
                httpResponse.sendTo(outputStream);
                return;
            }

            if (httpUrl.startsWith("/register") && httpMethod == HttpMethod.POST) {
                final Map<String, String> queryParams = parseQueryParam(httpBody);

                final String account = queryParams.get("account");
                final String email = queryParams.get("email");
                final String password = queryParams.get("password");

                if (account != null && email != null && password != null) {
                    InMemoryUserRepository.save(new User(account, password, email));
                    log.info("가입 성공, account = {}, email = {}, password = {}", account, email, password);
                }

                httpResponse.setResponseLine(HttpVersion.HTTP_1_1, HttpStatusCode.HTTP_STATUS_302,
                        new ReasonPhrase("Found"));

                httpResponse.putHeader(LOCATION, "/index.html");
                httpResponse.putHeader(CONTENT_LENGTH, "0");

                httpResponse.sendTo(outputStream);
                return;
            }

            if (httpUrl.startsWith("/index.html")) {
                final String body = readFile("static/index.html");

                httpResponse.setResponseLine(HttpVersion.HTTP_1_1, HttpStatusCode.HTTP_STATUS_200,
                        new ReasonPhrase("OK"));
                httpResponse.putHeader(CONTENT_TYPE, CONTENT_TYPE_TEXT_HTML);
                httpResponse.putHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
                httpResponse.setHttpBody(new HttpBody(body));
                httpResponse.sendTo(outputStream);
                return;
            }

            if (httpUrl.startsWith("/401.html")) {
                final String body = readFile("static/401.html");

                httpResponse.setResponseLine(HttpVersion.HTTP_1_1, HttpStatusCode.HTTP_STATUS_200,
                        new ReasonPhrase("OK"));
                httpResponse.putHeader(CONTENT_TYPE, CONTENT_TYPE_TEXT_HTML);
                httpResponse.putHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
                httpResponse.setHttpBody(new HttpBody(body));
                httpResponse.sendTo(outputStream);
                return;
            }

            if (httpUrl.startsWith("/css/styles.css")) {
                final String body = readFile("static/css/styles.css");

                httpResponse.setResponseLine(HttpVersion.HTTP_1_1, HttpStatusCode.HTTP_STATUS_200,
                        new ReasonPhrase("OK"));
                httpResponse.putHeader(CONTENT_TYPE, CONTENT_TYPE_TEXT_CSS);
                httpResponse.putHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
                httpResponse.setHttpBody(new HttpBody(body));
                httpResponse.sendTo(outputStream);
                return;
            }

            if (httpUrl.startsWith("/assets/chart-bar.js")) {
                final String body = readFile("static/assets/chart-bar.js");

                httpResponse.setResponseLine(HttpVersion.HTTP_1_1, HttpStatusCode.HTTP_STATUS_200,
                        new ReasonPhrase("OK"));
                httpResponse.putHeader(CONTENT_TYPE, CONTENT_TYPE_TEXT_JAVASCRIPT);
                httpResponse.putHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
                httpResponse.setHttpBody(new HttpBody(body));
                httpResponse.sendTo(outputStream);
                return;
            }

            if (httpUrl.startsWith("/js/scripts.js")) {
                final String body = readFile("static/js/scripts.js");

                httpResponse.setResponseLine(HttpVersion.HTTP_1_1, HttpStatusCode.HTTP_STATUS_200,
                        new ReasonPhrase("OK"));
                httpResponse.putHeader(CONTENT_TYPE, CONTENT_TYPE_TEXT_JAVASCRIPT);
                httpResponse.putHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
                httpResponse.setHttpBody(new HttpBody(body));
                httpResponse.sendTo(outputStream);
                return;
            }

            if (httpUrl.startsWith("/assets/chart-pie.js")) {
                final String body = readFile("static/assets/chart-pie.js");

                httpResponse.setResponseLine(HttpVersion.HTTP_1_1, HttpStatusCode.HTTP_STATUS_200,
                        new ReasonPhrase("OK"));
                httpResponse.putHeader(CONTENT_TYPE, CONTENT_TYPE_TEXT_JAVASCRIPT);
                httpResponse.putHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
                httpResponse.setHttpBody(new HttpBody(body));
                httpResponse.sendTo(outputStream);
                return;
            }

            if (httpUrl.startsWith("/assets/chart-area.js")) {
                final String body = readFile("static/assets/chart-area.js");

                httpResponse.setResponseLine(HttpVersion.HTTP_1_1, HttpStatusCode.HTTP_STATUS_200,
                        new ReasonPhrase("OK"));
                httpResponse.putHeader(CONTENT_TYPE, CONTENT_TYPE_TEXT_JAVASCRIPT);
                httpResponse.putHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
                httpResponse.setHttpBody(new HttpBody(body));
                httpResponse.sendTo(outputStream);
                return;
            }

            httpResponse.setResponseLine(HttpVersion.HTTP_1_1, HttpStatusCode.HTTP_STATUS_200,
                    new ReasonPhrase("OK"));
            httpResponse.putHeader(CONTENT_TYPE, CONTENT_TYPE_TEXT_HTML);
            httpResponse.putHeader(CONTENT_LENGTH, String.valueOf("Hello world".getBytes().length));
            httpResponse.setHttpBody(new HttpBody("Hello world"));
            httpResponse.sendTo(outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readFile(String path) throws IOException {
        final URL url = getClass().getClassLoader().getResource(path);
        return new String(Files.readAllBytes(new File(url.getFile()).toPath()), StandardCharsets.UTF_8);
    }

    private Map<String, String> parseQueryParam(String queryLine) {
        final String[] params = queryLine.split(PARAM_DELIMITER);

        final Map<String, String> queries = new HashMap<>();
        for (String param : params) {
            final String[] keyToken = param.split(KEY_VALUE_DELIMITER);
            queries.put(URLDecoder.decode(keyToken[0], StandardCharsets.UTF_8),
                    URLDecoder.decode(keyToken[1], StandardCharsets.UTF_8));
        }
        return queries;
    }
}
