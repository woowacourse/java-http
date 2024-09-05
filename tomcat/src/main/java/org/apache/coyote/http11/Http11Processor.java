package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final int REQUEST_LINE_PARAM_COUNT = 3;
    private static final Set<String> ALLOWED_METHODS =
            Set.of("GET", "HEAD", "POST", "PUT", "DELETE", "CONNECT", "OPTIONS", "TRACE", "PATCH");
    private static final String HTTP_1_1 = "HTTP/1.1";
    private static final String STATIC_DIRNAME = "static";
    private static final String NOT_FOUND_FILENAME = "404.html";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String JSESSIONID = "JSESSIONID";

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

            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            final String requestLine = readRequestLine(reader);
            Map<String, String> headers = readHeaders(reader);
            String requestBody = null;
            HttpCookie cookie = null;

            if (headers.containsKey(CONTENT_LENGTH)) {
                int contentLength = Integer.parseInt(headers.get(CONTENT_LENGTH));
                char[] buffer = new char[contentLength];
                reader.read(buffer, 0, contentLength);
                requestBody = new String(buffer);
            }

            if (headers.containsKey(COOKIE)) {
                cookie = new HttpCookie(headers.get(COOKIE));
            }

            final String response = makeResponse(requestLine, requestBody, cookie);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readRequestLine(BufferedReader reader) {
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new UncheckedServletException("HTTP 요청의 request line을 읽을 수 없습니다.");
        }
    }

    private Map<String, String> readHeaders(BufferedReader reader) {
        Map<String, String> headers = new HashMap<>();

        try {
            while (reader.ready()) {
                String line = reader.readLine();

                if (line.isBlank()) {
                    break;
                }

                String[] entry = line.split(": ");

                if (entry.length == 2) {
                    headers.put(entry[0], entry[1]);
                }
            }
            return headers;
        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
    }

    private String makeResponse(String requestLine, String requestBody, HttpCookie cookie) {
        String[] params = requestLine.split(" ");
        validateParamCount(params);

        String method = params[0];
        String requestUri = params[1];
        String httpVersion = params[2];
        validateFormat(method, requestUri, httpVersion);

        String endpoint = parseEndpoint(requestUri);
        Map<String, String> queryParams = parseQueryParams(requestUri);
        Map<String, String> requestData = parseRequestBody(requestBody);

        if (method.equals("GET") && endpoint.equals("/")) {
            return makeResponseMessageFromText("Hello world!", HttpStatusCode.OK);
        }

        if (method.equals("GET") && endpoint.equals("/login")) {
            if (cookie != null && cookie.hasCookieWithName(JSESSIONID)) {
                SessionManager sessionManager = SessionManager.getInstance();
                String sessionId = cookie.get(JSESSIONID);
                if (sessionManager.findSession(sessionId).isPresent()) {
                    return makeResponseMessageFromFile("index.html", HttpStatusCode.FOUND, cookie);
                }
            }

            return makeResponseMessageFromFile("login.html", HttpStatusCode.OK, cookie);
        }

        if (method.equals("POST") && endpoint.equals("/login")) {
            if (requestData.containsKey("account") && requestData.containsKey("password")) {
                String account = requestData.get("account");
                String password = requestData.get("password");

                return InMemoryUserRepository.findByAccountAndPassword(account, password)
                        .map(user -> {
                            SessionManager sessionManager = SessionManager.getInstance();
                            Session session = new Session();
                            session.setAttribute("user", user);
                            sessionManager.add(cookie.get(JSESSIONID), session);
                            return makeResponseMessageFromFile("index.html", HttpStatusCode.FOUND, cookie);
                        })
                        .orElseGet(() -> makeResponseMessageFromFile("401.html", HttpStatusCode.UNAUTHORIZED, cookie));
            }
        }

        if (method.equals("POST") && endpoint.equals("/register")) {
            if (requestData.containsKey("account")
                && requestData.containsKey("email")
                && requestBody.contains("password")
            ) {
                String account = requestData.get("account");
                String email = requestData.get("email");
                String password = requestData.get("password");

                if (!InMemoryUserRepository.existsByAccount(account)) {
                    InMemoryUserRepository.save(new User(account, password, email));
                }

                return makeResponseMessageFromFile("index.html", HttpStatusCode.FOUND, cookie);
            }
        }

        return makeResponseMessageFromFile(endpoint.substring(1), HttpStatusCode.OK, cookie);
    }

    private static void validateParamCount(String[] params) {
        if (params.length != REQUEST_LINE_PARAM_COUNT) {
            throw new UncheckedServletException(
                    String.format(
                            "Request line의 인자는 %d개여야 합니다. 입력된 인자 개수 = %d",
                            REQUEST_LINE_PARAM_COUNT,
                            params.length
                    )
            );
        }
    }

    private void validateFormat(String method, String requestUri, String httpVersion) {
        if (!ALLOWED_METHODS.contains(method)) {
            throw new UncheckedServletException(String.format("허용되지 않는 HTTP method입니다. 입력값 = %s", method));
        }
        if (!requestUri.startsWith("/")) {
            throw new UncheckedServletException(String.format("Request URI는 /로 시작하여야 합니다. 입력값 = %s", requestUri));
        }
        if (!httpVersion.equals(HTTP_1_1)) {
            throw new UncheckedServletException(String.format("HTTP 버전은 %s만 허용됩니다. 입력값 = %s", HTTP_1_1, httpVersion));
        }
    }

    private String parseEndpoint(String requestUri) {
        return requestUri.split("\\?")[0];
    }

    private Map<String, String> parseQueryParams(String requestUri) {
        String[] substrings = requestUri.split("\\?");

        if (substrings.length == 1) {
            return Collections.emptyMap();
        }

        Map<String, String> queryParams = new HashMap<>();
        String rawQueryParams = requestUri.split("\\?")[1];

        for (String entry : rawQueryParams.split("&")) {
            String[] data = entry.split("=");
            if (data.length != 2) {
                return Collections.emptyMap();
            }
            queryParams.put(data[0], data[1]);
        }

        return queryParams;
    }

    private Map<String, String> parseRequestBody(String requestBody) {
        if (requestBody == null) {
            return Collections.emptyMap();
        }

        Map<String, String> requestData = new HashMap<>();

        for (String entry : requestBody.split("&")) {
            String[] data = entry.split("=");
            if (data.length == 2) {
                requestData.put(data[0], data[1]);
            }
        }

        return requestData;
    }

    private String makeResponseMessageFromText(String content, HttpStatusCode statusCode) {
        return String.join(
                "\r\n",
                "HTTP/1.1 " + statusCode.getValue() + " ",
                CONTENT_TYPE + ": " + "text/html;charset=utf-8 ",
                CONTENT_LENGTH + ": " + content.getBytes().length + " ",
                "",
                content
        );
    }

    private String makeResponseMessageFromFile(String fileName, HttpStatusCode statusCode, HttpCookie cookie) {
        if (!fileName.contains(".")) {
            fileName += ".html";
        }
        String responseBody = readBody(fileName);
        String contentType = "";

        if (fileName.endsWith(".html")) {
            contentType = "text/html";
        }

        if (fileName.endsWith(".css")) {
            contentType = "text/css";
        }

        if (fileName.endsWith(".svg")) {
            contentType = "image/svg+xml";
        }

        if (fileName.endsWith(".js")) {
            contentType = "text/javascript";
        }

        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, contentType + ";charset=utf-8 ");
        headers.put(CONTENT_LENGTH, responseBody.getBytes().length + " ");

        if (cookie == null || !cookie.hasCookieWithName(JSESSIONID)) {
            HttpCookie httpCookie = new HttpCookie();
            httpCookie.add(JSESSIONID, UUID.randomUUID().toString());
            headers.put(SET_COOKIE, httpCookie.getCookiesAsString());
        }

        String startLineText = "HTTP/1.1 " + statusCode.getValue() + " ";

        String headerText = headers.keySet().stream()
                .map(headerName -> headerName + ": " + headers.get(headerName))
                .collect(Collectors.joining(("\r\n")));

        return String.join("\r\n", startLineText, headerText, "", responseBody);
    }

    private String readBody(String fileName) {
        try {
            URI uri = getClass().getClassLoader().getResource(STATIC_DIRNAME + "/" + fileName).toURI();
            Path path = Paths.get(uri);
            return Files.readString(path);
        } catch (NullPointerException e) {
            return readBody(NOT_FOUND_FILENAME);
        } catch (Exception e) {
            throw new UncheckedServletException(e);
        }
    }
}
