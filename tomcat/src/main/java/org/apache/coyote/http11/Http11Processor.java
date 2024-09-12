package org.apache.coyote.http11;

import static org.apache.coyote.http11.domain.HttpStatusCode.FOUND;
import static org.apache.coyote.http11.domain.HttpStatusCode.OK;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.domain.Cookie;
import org.apache.coyote.http11.domain.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE = "Cookie";
    private static final String PARAMETER_SEPARATOR = "&";
    private static final String ASSIGN_OPERATOR = "=";
    private static final String HEADER_DELIMITER = ": ";

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
             final var outputStream = connection.getOutputStream();
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String[] requestLine = reader.readLine().split(" ");
            String method = requestLine[0];
            String url = requestLine[1];
            String version = requestLine[2];

            Map<String, String> requestHeaders = readRequestHeaders(reader);

            if (method.equals(GET)) {
                doGet(url, version, requestHeaders, outputStream);
            }
            if (method.equals(POST)) {
                doPost(url, version, requestHeaders, reader, outputStream);
            }

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> readRequestHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> requestHeaders = new HashMap<>();
        String line;
        while (!(line = bufferedReader.readLine()).isEmpty()) {
            String[] header = line.split(HEADER_DELIMITER);
            System.out.println(header[0]);
            requestHeaders.put(header[0], header[1]);
        }
        return Collections.unmodifiableMap(requestHeaders);
    }

    private void doGet(
            String url,
            String version,
            Map<String, String> requestHeaders,
            OutputStream outputStream
    ) throws IOException {
        if (url.equals("/")) {
            createHttpResponse(
                    "Hello world!",
                    createHttpResponseHeader(version + " 200 OK ", "text/html", "Hello world!"),
                    outputStream);
            return;
        }
        String[] result = determineGetResponse(url, version, requestHeaders);
        Path path = getPath(result);
        var responseBody = Files.readString(path);
        String contentType = Files.probeContentType(path);

        String responseHeader = createHttpResponseHeader(result[0], contentType, responseBody);
        createHttpResponse(responseBody, responseHeader, outputStream);
    }

    private String[] determineGetResponse(String url, String version, Map<String, String> requestHeaders) {
        String[] result = new String[2];
        result[0] = version + " " + OK.toString() + " ";
        if (url.endsWith("html") || url.endsWith("js") || url.endsWith("css")) {
            result[1] = "static" + url;
            return result;
        }
        if (url.equals("/login") && isAlreadyLogin(requestHeaders)) {
            result[0] = createResponseHeader(version, FOUND, "/index.html");
            result[1] = "static/index.html";

        }
        result[1] = "static" + url + ".html";
        return result;
    }

    private Path getPath(String[] pathAndStatus) {
        URL resource = getClass().getClassLoader().getResource(pathAndStatus[1]);
        return new File(resource.getPath()).toPath();
    }

    private void doPost(
            String url,
            String version,
            Map<String, String> requestHeaders,
            BufferedReader reader,
            OutputStream outputStream
    ) throws IOException {
        String[] requestBody = readRequestBody(requestHeaders, reader);
        String[] responseHeaderAndPath = determinePostResponse(url, version, requestBody);

        final Path path = getPath(responseHeaderAndPath);
        var responseBody = Files.readString(path);
        String contentType = Files.probeContentType(path);

        String responseHeader = createHttpResponseHeader(responseHeaderAndPath[0], contentType, responseBody);
        createHttpResponse(responseBody, responseHeader, outputStream);
    }

    private String[] determinePostResponse(String url, String version, String[] requestBody) {
        String[] result = new String[2];
        if (url.startsWith("/login")) {
            result = doLogin(version, requestBody);
        }
        if (url.equals("/register")) {
            result = doRegister(version, requestBody);
        }
        return result;
    }

    private String[] doLogin(String version, String[] requestBody) {
        String[] result = new String[2];
        result[0] = createResponseHeader(version, FOUND, "/index.html");
        result[1] = "static/index.html";

        String account = requestBody[0].split(ASSIGN_OPERATOR)[1];
        String password = requestBody[1].split(ASSIGN_OPERATOR)[1];

        User user = InMemoryUserRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        if (!user.checkPassword(password)) {
            result[1] = "static/401.html";
            result[0] = createResponseHeader(version, FOUND, "/401.html");
            return result;
        }
        log.info(user.toString());

        Cookie cookie = createCookie();
        createSession(user, cookie.getSessionId());
        result[0] = String.join("\r\n",
                result[0],
                "Set-Cookie: " + JSESSIONID + "=" + cookie.getSessionId());
        return result;
    }

    private static String createResponseHeader(String version, HttpStatusCode statusCode, String location) {
        return String.join("\r\n",
                version + " " + statusCode.toString() + " ",
                "Location: " + location + " ");
    }

    private boolean isAlreadyLogin(Map<String, String> requestHeaders) {
        if (requestHeaders.containsKey(COOKIE)) {
            String sessionId = requestHeaders.get(COOKIE).split(ASSIGN_OPERATOR)[1];
            SessionManager sessionManager = SessionManager.getInstance();
            Session session = sessionManager.findSession(sessionId);
            return session != null;
        }
        return false;
    }

    private Cookie createCookie() {
        String sessionId = UUID.randomUUID().toString();
        Cookie cookie = new Cookie();
        cookie.setCookie(JSESSIONID, sessionId);
        return cookie;
    }

    private void createSession(User user, String sessionId) {
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = new Session(sessionId);
        sessionManager.add(session);
        session.setAttribute("user", user);
    }

    private String[] readRequestBody(Map<String, String> requestHeaders, BufferedReader reader) throws IOException {
        int contentLength = Integer.parseInt(requestHeaders.get("Content-Length"));
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer).split(PARAMETER_SEPARATOR);
    }

    private String[] doRegister(String version, String[] requestBody) {
        String[] result = new String[2];
        result[0] = createResponseHeader(version, FOUND, "/index.html");
        result[1] = "static/index.html";
        User user = createUser(requestBody);
        InMemoryUserRepository.save(user);

        return result;
    }

    private User createUser(String[] requestBody) {
        Map<String, String> userInfo = new HashMap<>();
        for (String query : requestBody) {
            String[] param = query.split(ASSIGN_OPERATOR);
            userInfo.put(param[0], param[1]);
        }
        return new User(userInfo.get("account"), userInfo.get("password"), userInfo.get("email"));
    }

    private static String createHttpResponseHeader(String responseHeader, String contentType, String responseBody) {
        return String.join("\r\n",
                responseHeader,
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ");
    }

    private void createHttpResponse(String responseBody, String responseHeader, OutputStream outputStream)
            throws IOException {
        final var response = String.join("\r\n",
                responseHeader,
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}