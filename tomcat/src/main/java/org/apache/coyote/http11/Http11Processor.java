package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
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

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String requestLine = br.readLine();

            if (requestLine == null || requestLine.isBlank()) {
                return;
            }

            String[] requestLineInfo = requestLine.split(" ");
            if (requestLineInfo.length < 2) {
                return;
            }

            String httpMethod = requestLineInfo[0];
            String url = requestLineInfo[1];
            String response;

            StringBuilder header = new StringBuilder();
            String line;
            int contentLength = 0;
            String cookieHeader = "";
            while ((line = br.readLine()) != null && !line.isBlank()) {
                header.append(line).append("\r\n");
                if (line.startsWith("Content-Length:")) {
                    String lengthStr = line.substring("Content-Length:".length()).trim();
                    contentLength = Integer.parseInt(lengthStr);
                }
                if (line.startsWith("Cookie: ")) {
                    cookieHeader = line.substring("Cookie:".length()).trim();
                }
            }

            StringBuilder body = new StringBuilder();
            if (contentLength > 0) {
                char[] bodyChars = new char[contentLength];
                br.read(bodyChars, 0, contentLength);
                body.append(bodyChars);
            }

            HttpCookie cookie = new HttpCookie(cookieHeader);
            if (url.equals("/")) {
                response = sendDefaultResource();
                sendResponse(outputStream, response);
                return;
            }

            if (httpMethod.equals("GET") && !url.contains("?")) {
                String staticUrl = "static" + url;
                if (!url.contains(".")) {
                    staticUrl += ".html";
                }
                response = getResponse(staticUrl, OK);
                sendResponse(outputStream, response);
                return;
            }

            if (httpMethod.equals("POST")&& url.equals("/login")) {
                response = loginUserResponse(body, cookie);
                sendResponse(outputStream, response);
                return;
            }

            if (httpMethod.equals("POST") && url.equals("/register")) {
                response = registerUserResponse(body);
                sendResponse(outputStream, response);
                return;
            }

            response = getResponse("static/404.html", "404 Not Found");
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

    private String loginUserResponse(StringBuilder body, HttpCookie cookie) throws URISyntaxException, IOException {
        Map<String, String> queryParams = parseQueryParams(body.toString());
        String account = queryParams.get("account");
        String password = queryParams.get("password");

        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isEmpty()) {
            return getResponse("static/404.html", NOT_FOUND);
        }
        if (user.get().checkPassword(password)) {
            log.info(user.toString());
            if (!cookie.hasJsessionid()){
                String jsessionid = UUID.randomUUID().toString();
                String cookieHeaderValue = "JSESSIONID=" + jsessionid;
                return getResponse("static/index.html", FOUND, cookieHeaderValue);
            }
            return getResponse("static/index.html", FOUND);
        }
        return getResponse("static/401.html", UNAUTHORIZED);
    }

    private String registerUserResponse(StringBuilder body) throws IOException, URISyntaxException {
        Map<String, String> formData = parseQueryParams(body.toString());
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

    private String getResponse(String uri, String statusCode, String setCookie) throws IOException, URISyntaxException {
        final var path = Paths.get(findUri(uri));
        final var contentType = Files.probeContentType(path);
        final byte[] responseBodyBytes = Files.readAllBytes(path);
        final String responseBody = Files.readString(path);

        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBodyBytes.length + " ",
                "Set-Cookie: " + setCookie,
                "",
                responseBody);
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
