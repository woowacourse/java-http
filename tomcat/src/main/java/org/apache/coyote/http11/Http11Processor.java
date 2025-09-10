package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.Processor;
import org.apache.coyote.cookie.HttpCookie;
import org.apache.coyote.http11.parser.HttpRequestParser;
import org.apache.coyote.session.HttpSession;
import org.apache.coyote.session.HttpSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final HttpSessionManager SESSION_MANAGER = new HttpSessionManager();
    private static final String OK = "200 OK";
    private static final String FOUND = "302 Found";
    private static final String BAD_REQUEST = "400 Bad Request";
    private static final String UNAUTHORIZED = "401 Unauthorized";
    private static final String NOT_FOUND = "404 Not Found";
    private static final String INTERNAL_SERVER_ERROR = "500 Internal Server Error";

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

            HttpRequest request = HttpRequestParser.parse(inputStream);
            String response;

            if (request.getUrl().equals("/")) {
                response = sendDefaultResource();
                sendResponse(outputStream, response);
                return;
            }

            if (request.getMethod().equals("GET") && request.getUrl().equals("/login")){
                String sessionId = request.getCookie().getJsessionid();
                if (sessionId != null && SESSION_MANAGER.containsKey(sessionId)) {
                    response = getRedirectResponse("/index.html", FOUND, null);
                } else {
                    response = getResponse("static/login.html", OK);
                }
                sendResponse(outputStream, response);
                return;
            }

            if (request.getMethod().equals("GET") && !request.getUrl().contains("?")) {
                String staticUrl = "static" + request.getUrl();
                if (!request.getUrl().contains(".")) {
                    staticUrl += ".html";
                }
                response = getResponse(staticUrl, OK);
                sendResponse(outputStream, response);
                return;
            }

            if (request.getMethod().equals("POST")&& request.getUrl().equals("/login")) {
                response = loginUserResponse(request.getBody(), request.getCookie());
                sendResponse(outputStream, response);
                return;
            }

            if (request.getMethod().equals("POST") && request.getUrl().equals("/register")) {
                response = registerUserResponse(request.getBody());
                sendResponse(outputStream, response);
                return;
            }

            response = getResponse("static/404.html", NOT_FOUND);
            sendResponse(outputStream, response);

        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String sendDefaultResource() throws IOException {
        final var responseBody = "Hello world!";
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );
    }

    private void sendResponse(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private String loginUserResponse(String body, HttpCookie cookie) throws URISyntaxException, IOException {
        Map<String, String> queryParams = parseQueryParams(body);
        String account = queryParams.get("account");
        String password = queryParams.get("password");
        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isEmpty()) {
            return getResponse("static/401.html", UNAUTHORIZED);
        }
        if (user.get().checkPassword(password)) {
            log.info(user.toString());

            String jsessionid = cookie.getJsessionid();
            if (jsessionid.isEmpty()) {
                jsessionid = UUID.randomUUID().toString();
            }
            HttpSession session = new HttpSession(jsessionid);
            session.setAttribute("user", user);
            SESSION_MANAGER.add(session);

            String cookieHeaderValue = "JSESSIONID=" + jsessionid;

            return getRedirectResponse("/index.html", FOUND, cookieHeaderValue);
        }

        return getRedirectResponse("static/401.html", UNAUTHORIZED, null);
    }

    private String registerUserResponse(String body) throws IOException, URISyntaxException {
        Map<String, String> formData = parseQueryParams(body);
        String account = formData.get("account");
        String password = formData.get("password");
        String email = formData.get("email");

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return getResponse("static/register.html", BAD_REQUEST);
        }
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return getResponse("static/index.html", OK);
    }

    private String getResponse(String uri, String statusCode) throws IOException, URISyntaxException {
        final var path = Paths.get(findUri(uri));
        final var contentType = Files.probeContentType(path);
        final byte[] responseBodyBytes = Files.readAllBytes(path);
        final String responseBody = Files.readString(path);

        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBodyBytes.length + " ",
                "",
                responseBody);
    }

    private String getRedirectResponse(String location, String statusCode, String setCookie) throws IOException, URISyntaxException {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("HTTP/1.1 ").append(statusCode).append("\r\n");
        responseBuilder.append("Location: ").append(location).append("\r\n");

        if (setCookie != null && !setCookie.isBlank()) {
            responseBuilder.append("Set-Cookie: ").append(setCookie).append("\r\n");
        }
        responseBuilder.append("\r\n");

        return responseBuilder.toString();
    }

    private URI findUri(String staticUrl) throws URISyntaxException {
        final var resource = getClass().getClassLoader().getResource(staticUrl);

        if (resource == null) {
            return Objects.requireNonNull(getClass().getClassLoader().getResource("static/404.html")).toURI();
        }
        return resource.toURI();
    }

    private Map<String, String> parseQueryParams(String queryString) {
        Map<String, String> queryParams = new HashMap<>();
        String[] params = queryString.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=", 2);
            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                queryParams.put(key, value);
            }
        }
        return queryParams;
    }
}
