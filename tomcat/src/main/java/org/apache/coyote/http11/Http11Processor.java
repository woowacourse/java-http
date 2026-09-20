package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
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
    private static final String START_LINE_DELIMITER = " ";
    private static final String CONTENT_TYPE_TEXT_HTML = "text/html;charset=utf-8";
    private static final String CONTENT_TYPE_TEXT_CSS = "text/css;charset=utf-8";
    private static final String CONTENT_TYPE_TEXT_JAVASCRIPT = "text/javascript;charset=utf-8";
    private static final String PARAM_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String USER = "user";

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

            // Parse StartLine
            final String[] startLineTokens = bufferedReader.readLine().split(START_LINE_DELIMITER);
            final HttpMethod httpMethod = HttpMethod.valueOf(startLineTokens[0]);
            final String httpUrl = startLineTokens[1];
            final HttpVersion httpVersion = HttpVersion.getByString(startLineTokens[2]);

            // Parse Headers
            final Map<String, String> headers = parseHeaders(bufferedReader);

            // Prase Body
            final String httpBody = parseBody(bufferedReader, headers.get(CONTENT_LENGTH));

            // Parse Cookie
            final HttpCookie httpCookie = new HttpCookie(headers.get(COOKIE));

            log.info("HttpRequest method: {}, URL: {}, Version: {}", httpMethod.name(), httpUrl, httpVersion.name());

            if (httpUrl.startsWith("/login") && httpMethod == HttpMethod.GET) {
                // 세션이 유효하면 index.html로 리다이렉트한다.
                if (httpCookie.get(JSESSIONID) != null) {
                    final HttpSession session = SessionManager.getInstance().findSession(httpCookie.get(JSESSIONID));

                    if (session != null) {
                        final String response = createRedirectResponse("/index.html");

                        outputStream.write(response.getBytes());
                        outputStream.flush();
                        return;
                    }
                }

                final String body = readFile("static/login.html");
                final var response = createResponse(body, CONTENT_TYPE_TEXT_HTML);

                outputStream.write(response.getBytes());
                outputStream.flush();
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

                        final String response = createResponseWithCookieAndRedirect("/index.html",
                                new Cookie(JSESSIONID, session.getId()));

                        outputStream.write(response.getBytes());
                        outputStream.flush();
                        return;
                    }

                    // 로그인에 실패했다면 401 페이지로 리다이렉트한다.
                    final String response = createRedirectResponse("/401.html");

                    outputStream.write(response.getBytes());
                    outputStream.flush();
                    return;
                }
            }

            if (httpUrl.startsWith("/register") && httpMethod == HttpMethod.GET) {
                final String body = readFile("static/register.html");
                final var response = createResponse(body, CONTENT_TYPE_TEXT_HTML);

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (httpUrl.startsWith("/register") && httpMethod == HttpMethod.POST) {
                final var response = createRedirectResponse("/index.html");
                final Map<String, String> queryParams = parseQueryParam(httpBody);

                final String account = queryParams.get("account");
                final String email = queryParams.get("email");
                final String password = queryParams.get("password");

                if (account != null && email != null && password != null) {
                    InMemoryUserRepository.save(new User(account, password, email));
                }

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (httpUrl.startsWith("/index.html")) {
                final String body = readFile("static/index.html");
                final var response = createResponse(body, CONTENT_TYPE_TEXT_HTML);

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (httpUrl.startsWith("/401.html")) {
                final String body = readFile("static/401.html");
                final var response = createResponse(body, CONTENT_TYPE_TEXT_HTML);

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (httpUrl.startsWith("/css/styles.css")) {
                final String body = readFile("static/css/styles.css");
                final var response = createResponse(body, CONTENT_TYPE_TEXT_CSS);

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (httpUrl.startsWith("/assets/chart-bar.js")) {
                final String body = readFile("static/assets/chart-bar.js");
                final var response = createResponse(body, CONTENT_TYPE_TEXT_JAVASCRIPT);

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (httpUrl.startsWith("/js/scripts.js")) {
                final String body = readFile("static/js/scripts.js");
                final var response = createResponse(body, CONTENT_TYPE_TEXT_JAVASCRIPT);

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (httpUrl.startsWith("/assets/chart-pie.js")) {
                final String body = readFile("static/assets/chart-pie.js");
                final var response = createResponse(body, CONTENT_TYPE_TEXT_JAVASCRIPT);

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            if (httpUrl.startsWith("/assets/chart-area.js")) {
                final String body = readFile("static/assets/chart-area.js");
                final var response = createResponse(body, CONTENT_TYPE_TEXT_JAVASCRIPT);

                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            final var response = createResponse("Hello world!", CONTENT_TYPE_TEXT_HTML);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> parseHeaders(BufferedReader bufferedReader) throws IOException {
        final HashMap<String, String> headers = new HashMap<>();

        String line;
        while (!(line = bufferedReader.readLine()).isEmpty()) {
            // 가장 왼쪽의 콜론을 기준으로 파싱한다.
            final int firstColonIndex = line.indexOf(':');
            if (firstColonIndex == -1) {
                throw new IllegalArgumentException("헤더 포맷이 잘못되었습니다.");
            }

            final String key = line.substring(0, firstColonIndex).strip();
            final String value = line.substring(firstColonIndex + 1).strip();
            headers.put(key, value);
        }
        return headers;
    }

    private String parseBody(BufferedReader bufferedReader, String contentLengthString) throws IOException {
        if (contentLengthString == null) {
            return "";
        }
        final int contentLength = Integer.parseInt(contentLengthString);

        if (contentLength > 0) {
            final char[] buffer = new char[contentLength];
            int totalRead = 0;

            while (totalRead < contentLength) {
                final int read = bufferedReader.read(buffer, totalRead, contentLength - totalRead);
                if (read == -1) {
                    throw new IOException("Content-Length와 Body 길이가 일치하지 않습니다.");
                }
                totalRead += read;
            }

            return new String(buffer);
        }
        return "";
    }

    private String readFile(String path) throws IOException {
        final URL url = getClass().getClassLoader().getResource(path);
        return new String(Files.readAllBytes(new File(url.getFile()).toPath()), StandardCharsets.UTF_8);
    }

    private String createResponse(String responseBody, String contentType) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                CONTENT_TYPE + ": " + contentType + " ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String createRedirectResponse(String redirectUrl) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + redirectUrl + " ",
                "Content-Length: 0 ",
                "");
    }

    private String createResponseWithCookieAndRedirect(String redirectUrl, Cookie cookie) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Set-Cookie" + ": " + cookie.getName() + "=" + cookie.getValue() + " ",
                "Location: " + redirectUrl + " ",
                "Content-Length: 0 ",
                "");
    }

    private Map<String, String> parseQueryParam(String queryLine) {
        final String[] params = queryLine.split(PARAM_DELIMITER);

        final Map<String, String> queries = new HashMap<>();
        for (String param : params) {
            final String[] keyToken = param.split(KEY_VALUE_DELIMITER);
            queries.put(keyToken[0], keyToken[1]);
        }
        return queries;
    }
}
