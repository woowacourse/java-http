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
import java.util.List;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.request.HttpHeader;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.request.RequestLine;
import org.apache.coyote.response.ContentType;
import org.apache.coyote.response.HttpResponse;
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
    private static final String USER_SESSION_NAME = "user";

    private final Socket connection;
    private final SessionManager sessionManager;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.sessionManager = SessionManager.getInstance();
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

            final String response = handle(new HttpRequest(requestLine, httpHeader, requestBody));

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

    private String handle(HttpRequest request) {
        HttpCookie cookie = new HttpCookie(request.getHeader(COOKIE));
        RequestBody requestBody = request.getRequestBody();

        if (request.pointsTo(GET, "/")) {
            return buildTextMessage("Hello world!", HttpStatusCode.OK);
        }

        if (request.pointsTo(GET, "/login")) {
            return getLoginPage(cookie);
        }

        if (request.pointsTo(POST, "/login")) {
            return login(cookie, requestBody);
        }

        if (request.pointsTo(POST, "/register")) {
            return saveUser(cookie, requestBody);
        }

        return buildMessage(request.getPath(), HttpStatusCode.OK, cookie);
    }

    private String buildTextMessage(String content, HttpStatusCode statusCode) {
        HttpResponse httpResponse = new HttpResponse(statusCode, content, ContentType.TEXT_HTML);
        return httpResponse.buildMessage();
    }

    private String getLoginPage(HttpCookie cookie) {
        if (cookie.contains(JSESSIONID) && sessionManager.hasId(cookie.get(JSESSIONID))) {
            return buildMessage("index.html", HttpStatusCode.FOUND, cookie);
        }

        return buildMessage("login.html", HttpStatusCode.OK, cookie);
    }

    private String login(HttpCookie cookie, RequestBody requestBody) {
        if (requestBody.containsAll("account", "password")) {
            String account = requestBody.get("account");
            String password = requestBody.get("password");

            if (InMemoryUserRepository.existsByAccountAndPassword(account, password)) {
                User user = InMemoryUserRepository.getByAccount(account);
                Session session = new Session();
                session.setAttribute(USER_SESSION_NAME, user);
                sessionManager.add(cookie.get(JSESSIONID), session);
                return buildMessage("index.html", HttpStatusCode.FOUND, cookie);
            }

            return buildMessage("401.html", HttpStatusCode.UNAUTHORIZED, cookie);
        }

        throw new UncheckedServletException("올바르지 않은 Request Body 형식입니다.");
    }

    private String saveUser(HttpCookie cookie, RequestBody requestBody) {
        if (requestBody.containsAll("account", "email", "password")) {
            String account = requestBody.get("account");
            String email = requestBody.get("email");
            String password = requestBody.get("password");

            if (!InMemoryUserRepository.existsByAccount(account)) {
                InMemoryUserRepository.save(new User(account, password, email));
                return buildMessage("index.html", HttpStatusCode.FOUND, cookie);
            }

            throw new UncheckedServletException("이미 존재하는 ID입니다.");
        }

        throw new UncheckedServletException("올바르지 않은 Request Body 형식입니다.");
    }

    private String buildMessage(String fileName, HttpStatusCode statusCode, HttpCookie cookie) {
        if (!fileName.contains(".")) {
            fileName += ".html";
        }

        String responseBody = readFile(fileName);
        ContentType contentType = ContentType.fromFileExtension(
                fileName.substring(fileName.lastIndexOf(".")));

        HttpResponse httpResponse = new HttpResponse(statusCode, responseBody, contentType);

        if (cookie == null || !cookie.contains(JSESSIONID)) {
            HttpCookie httpCookie = new HttpCookie();
            httpCookie.add(JSESSIONID, UUID.randomUUID().toString());
            httpResponse.addHeader(SET_COOKIE, httpCookie.buildMessage());
        }

        return httpResponse.buildMessage();
    }

    private String readFile(String fileName) {
        try {
            URI uri = getClass().getClassLoader().getResource(STATIC_DIRNAME + "/" + fileName).toURI();
            Path path = Paths.get(uri);
            return Files.readString(path);
        } catch (NullPointerException e) {
            return readFile(NOT_FOUND_FILENAME);
        } catch (Exception e) {
            throw new UncheckedServletException(e);
        }
    }
}
