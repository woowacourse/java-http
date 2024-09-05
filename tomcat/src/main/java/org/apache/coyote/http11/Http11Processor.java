package org.apache.coyote.http11;

import static org.apache.coyote.request.HttpMethod.GET;
import static org.apache.coyote.request.HttpMethod.POST;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.request.HttpHeader;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.request.RequestLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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

            RequestLine requestLine = new RequestLine(reader.readLine());
            HttpHeader httpHeader = new HttpHeader(readRequestHeaders(reader));
            RequestBody requestBody = readRequestBody(reader, httpHeader);

            HttpRequest httpRequest = new HttpRequest(requestLine, httpHeader, requestBody);

            HttpCookie cookie = null;

            if (httpHeader.contains(COOKIE)) {
                cookie = new HttpCookie(httpHeader.get(COOKIE));
            }

            final String response = makeResponse(httpRequest, cookie);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> readRequestHeaders(BufferedReader reader) {
        try {
            List<String> rawHeaders = new ArrayList<>();

            while (reader.ready()) {
                String line = reader.readLine();
                if (line.isBlank()) {
                    break;
                }
                rawHeaders.add(line);
            }

            return rawHeaders;
        } catch (IOException e) {
            throw new UncheckedServletException("헤더를 읽는 데 실패하였습니다.");
        }
    }

    private RequestBody readRequestBody(BufferedReader reader, HttpHeader httpHeader) {
        try {
            if (httpHeader.contains(CONTENT_LENGTH)) {
                int contentLength = Integer.parseInt(httpHeader.get(CONTENT_LENGTH));
                char[] buffer = new char[contentLength];
                reader.read(buffer, 0, contentLength);
                return new RequestBody(new String(buffer));
            }
            return null;
        } catch (IOException e) {
            throw new UncheckedServletException("Request Body를 읽는 데 실패하였습니다.");
        }
    }

    private String makeResponse(HttpRequest httpRequest, HttpCookie cookie) {
        if (httpRequest.is(GET, "/")) {
            return makeResponseMessageFromText("Hello world!", HttpStatusCode.OK);
        }

        if (httpRequest.is(GET, "/login")) {
            if (cookie != null && cookie.hasCookieWithName(JSESSIONID)) {
                SessionManager sessionManager = SessionManager.getInstance();
                String sessionId = cookie.get(JSESSIONID);
                if (sessionManager.findSession(sessionId).isPresent()) {
                    return makeResponseMessageFromFile("index.html", HttpStatusCode.FOUND, cookie);
                }
            }

            return makeResponseMessageFromFile("login.html", HttpStatusCode.OK, cookie);
        }

        if (httpRequest.is(POST, "/login")) {
            RequestBody requestBody = httpRequest.getRequestBody();

            if (requestBody.containsAll("account", "password")) {
                String account = requestBody.get("account");
                String password = requestBody.get("password");

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

            throw new UncheckedServletException("올바르지 않은 Request Body 형식입니다.");
        }

        if (httpRequest.is(POST, "/register")) {
            RequestBody requestBody = httpRequest.getRequestBody();

            if (requestBody.containsAll("account", "email", "password")) {
                String account = requestBody.get("account");
                String email = requestBody.get("email");
                String password = requestBody.get("password");

                if (!InMemoryUserRepository.existsByAccount(account)) {
                    InMemoryUserRepository.save(new User(account, password, email));
                }

                return makeResponseMessageFromFile("index.html", HttpStatusCode.FOUND, cookie);
            }
        }

        return makeResponseMessageFromFile(httpRequest.getPath(), HttpStatusCode.OK, cookie);
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
