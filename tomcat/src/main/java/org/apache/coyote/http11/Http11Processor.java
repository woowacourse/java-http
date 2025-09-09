package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final int MAX_REQUEST_SIZE = 104_857_600; // 10MB

    private static final SessionManager SESSION_MANAGER = new SessionManager();
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
             final var outputStream = connection.getOutputStream()) {
            // request
            Request request = parseRequest(inputStream);
            validateHeader(request.header);
            String[] words = request.header.split(" ");
            String requestPath = words[1].split("\\?")[0];
            HttpMethod httpMethod = HttpMethod.from(words[0]);
            Cookies requestCookies = Cookies.from(request.header);
            Parameters parameters = null;
            if (request.header.split("\\?").length > 1) {
                parameters = parseParameters(request.header.split("\\?")[1]);
            }
            if (httpMethod == HttpMethod.POST) {
                parameters = parseParameters(request.body);
            }

            // response
            ContentType contentType = ContentType.NONE;
            HttpStatus httpStatus = HttpStatus.OK;
            Headers headers = new Headers();
            String responseBody = "";
            Cookies responseCookies = new Cookies();

            try {
                if (requestPath.equals("/login")) {
                    if (httpMethod == HttpMethod.GET) {
                        if (getLoggedUser(requestCookies) == null) {
                            requestPath = "/login.html";
                        } else {
                            requestPath = "/index.html";
                        }
                        contentType = ContentType.HTML;
                    }
                    if (httpMethod == HttpMethod.POST) {
                        login(parameters, responseCookies);
                        httpStatus = HttpStatus.FOUND;
                        contentType = ContentType.HTML;
                        headers.put("Location", "/index.html");
                    }
                }

                if (requestPath.equals("/register")) {
                    if (httpMethod == HttpMethod.GET) {
                        contentType = ContentType.HTML;
                        requestPath = "/register.html";
                    }
                    if (httpMethod == HttpMethod.POST) {
                        register(parameters);
                        httpStatus = HttpStatus.FOUND;
                        contentType = ContentType.HTML;
                        headers.put("Location", "/index.html");
                    }
                }

                if (requestPath.endsWith(".html")) {
                    contentType = ContentType.HTML;
                }
                if (requestPath.endsWith(".css")) {
                    contentType = ContentType.CSS;
                }
                if (requestPath.endsWith(".js")) {
                    contentType = ContentType.JAVASCRIPT;
                }
            } catch (UnauthorizedException e) {
                contentType = ContentType.HTML;
                httpStatus = HttpStatus.UNAUTHORIZED;
                headers.clear();
                requestPath = "/401.html";
            } catch (IllegalArgumentException e) {
                contentType = ContentType.HTML;
                httpStatus = HttpStatus.NOT_FOUND;
                headers.clear();
                requestPath = "/404.html";
            }

            if (contentType.isText() && !httpStatus.is3xx()) {
                responseBody = getStaticPage(requestPath);
            }
            final var response = buildResponse(httpStatus, contentType, headers, responseCookies, responseBody);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private User getLoggedUser(Cookies requestCookies) {
        String sessionId = requestCookies.get("JSESSIONID");
        if (sessionId == null) {
            return null;
        }
        Session session = SESSION_MANAGER.findSession(sessionId);
        if (session == null) {
            return null;
        }
        return (User)session.getAttribute("user");
    }

    // private static <T> T parseRequestBody(String request, Class<T> type) {
    //     String body = request.split("\r\n\r\n")[1];
    //     return RequestBodyParser.parse(body, type);
    // }

    private void validateHeader(String header) {
        if (header.split(" ").length < 3) {
            throw new IllegalArgumentException("유효하지 않은 요청 포맷입니다.");
        }
    }

    private String buildResponse(HttpStatus status, ContentType contentType, Headers headers, Cookies cookies,
        String responseBody) {
        int bodyLength = getBodyLength(responseBody);
        return "HTTP/1.1 " + status.getCode() + " " + status.getName() + "\r\n"
            + "Content-Type: " + contentType.getType() + ";charset=utf-8" + "\r\n"
            + "Content-Length: " + bodyLength + "\r\n"
            + addIfNotEmpty(headers.toString())
            + addIfNotEmpty(cookies.toString())
            + "\r\n" + responseBody;
    }

    private String addIfNotEmpty(String value) {
        if (value.isEmpty()) {
            return "";
        }
        return value + "\r\n";
    }

    private int getBodyLength(String responseBody) {
        if (responseBody == null) {
            return 0;
        }
        return responseBody.getBytes().length;
    }

    private String getStaticPage(String requestPath) throws IOException, URISyntaxException {
        String normalizedPath = Paths.get(requestPath).normalize().toString();
        if (normalizedPath.contains("..")) {
            throw new IllegalArgumentException("존재하지 않는 페이지입니다.");
        }
        if (normalizedPath.equals("/") || normalizedPath.equals("\\")) {
            return "Hello world!";
        }
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static" + normalizedPath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("존재하지 않는 페이지입니다.");
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void login(Parameters queryParams, Cookies responseCookies) {
        String account = queryParams.get("account");
        String password = queryParams.get("password");
        User user = findUser(account, password);
        Session session = new Session();
        session.setAttribute("user", user);
        SESSION_MANAGER.add(session);
        responseCookies.put("JSESSIONID", session.getId());
        log.info(user.toString());
    }

    private User findUser(String account, String password) {
        if (account == null || password == null) {
            throw new UnauthorizedException("필수 정보가 누락되었습니다.");
        }
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            throw new UnauthorizedException("존재하지 않는 사용자입니다.");
        }
        if (!user.get().checkPassword(password)) {
            throw new UnauthorizedException("비밀번호가 틀렸습니다.");
        }
        return user.get();
    }

    private void register(Parameters parameters) {
        String account = parameters.get("account");
        String email = parameters.get("email");
        String password = parameters.get("password");
        InMemoryUserRepository.findByAccount(account)
            .ifPresent(user -> {
                throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
            });
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    private Parameters parseParameters(String originalParams) {
        Parameters parameters = new Parameters();
        // String[] words = originalParams.split("\\?");
        for (var p : originalParams.split("&")) {
            parameters.put(p);
        }
        return parameters;
    }

    private Request parseRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        int byteSum = 0;
        String line;
        int contentLength = 0;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            sb.append(line).append("\r\n");
            byteSum += line.length() + 2;
            if (byteSum > MAX_REQUEST_SIZE) {
                throw new IllegalArgumentException("최대 크기를 초과한 요청입니다.");
            }

            // Content-Length 파싱
            if (line.toLowerCase().startsWith("content-length:")) {
                contentLength = Integer.parseInt(line.substring(15).trim());
            }
        }

        String body = "";
        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            int bytesRead = reader.read(buffer, 0, contentLength);
            body = new String(buffer, 0, bytesRead);
        }
        return new Request(sb.toString(), body);
    }

    private record Request(
        String header,
        String body
    ) {

    }
}
