package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final String RESOURCE_FILE_PREFIX = "static/";
    private static final String WHITESPACE_REGEX = " ";

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String QUESTION_MARK = "?";
    private static final String HTML_EXTENSION = ".html";
    private static final String CSS_EXTENSION = ".css";
    private static final String JS_EXTENSION = ".js";
    public static final String REDIRECTION_FOUND_CODE = "302";
    public static final String UNAUTHORIZED_CODE = "401";
    public static final String SUCCESS_CODE = "200";
    public static final String SUCCESS_STATUS_RESPONSE = "OK";
    public static final String ERROR_STATUS_RESPONSE = "ERROR";
    public static final String FOUND_STATUS_RESPONSE = "Found";
    public static final String INDEX = "index";
    public static final String INDEX_PAGE = "/index.html";
    public static final String UNAUTHORIZED_STATUS_RESPONSE = "Unauthorized";
    private static final String USER = "user";
    private static final String COOKIE = "Cookie";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String POST = "POST";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOGIN_PATH = "/login";
    private static final String REGISTER_PATH = "/register";

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

            BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String requestLine = bufferedReader.readLine();
            if (requestLine == null) {
                return;
            }

            final Map<String, String> requestHeaders = new HashMap<>();
            String line;
            while (!(line = bufferedReader.readLine()).isEmpty()) {
                String[] parsedLine = line.split(":", 2);
                if (parsedLine.length != 2) {
                    continue;
                }

                requestHeaders.put(parsedLine[0].strip(), parsedLine[1].strip());
            }

            String[] parts = requestLine.split(WHITESPACE_REGEX);
            String method = parts[0];
            String part = parts[1];

            if (POST.equals(method)
                && handlePost(part, requestHeaders, bufferedReader, outputStream)) {
                return;
            }

            if (isRootRequest(part, outputStream)) {
                return;
            }

            String requestUri = part.substring(1);
            String statusCode = SUCCESS_CODE;

            URL resource = getClass().getClassLoader()
                .getResource(RESOURCE_FILE_PREFIX + requestUri);

            if (requestUri.equals("login")) {
                if (isLoggedIn(requestHeaders)) {
                    sendRedirect(outputStream, INDEX_PAGE);
                    return;
                }

                resource = getClass().getClassLoader()
                    .getResource(RESOURCE_FILE_PREFIX + requestUri + HTML_EXTENSION);
            }

            if (requestUri.equals("register")) {
                resource = getClass().getClassLoader()
                    .getResource(RESOURCE_FILE_PREFIX + requestUri + HTML_EXTENSION);
            }

            if (requestUri.contains(QUESTION_MARK)) {
                int index = requestUri.indexOf(QUESTION_MARK);
                part = requestUri.substring(0, index);
                String queryString = requestUri.substring(index + 1);

                resource = getClass().getClassLoader()
                    .getResource(RESOURCE_FILE_PREFIX + part + HTML_EXTENSION);

                String[] splitQuery = queryString.split("&");
                String account = splitQuery[0].substring(8);
                String password = splitQuery[1].substring(9);

                User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalStateException("등록되지 않은 계정입니다."));

                if (loginSuccess(user, password, outputStream)) {
                    return;
                }
                if (!user.isMatchPassword(password)) {
                    statusCode = UNAUTHORIZED_CODE;
                    resource = getClass().getClassLoader()
                        .getResource(RESOURCE_FILE_PREFIX + UNAUTHORIZED_CODE + HTML_EXTENSION);
                }
            }

            if (validateURLIsNull(resource, outputStream)) {
                return;
            }

            Path path = new File(resource.getPath()).toPath();
            byte[] body = Files.readAllBytes(Path.of(resource.toURI()));

            String response = createResponse(part, body, path, statusCode);

            writeAndFlush(outputStream, response.getBytes());
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean loginSuccess(User user, String password, OutputStream outputStream)
        throws IOException {
        if (!user.isMatchPassword(password)) {
            return false;
        }

        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute(USER, user);
        SessionManager.getInstance().add(session);

        log.info("login success: {}, sessionId: {}", user, session.getId());
        sendRedirect(outputStream, INDEX_PAGE, session.getId());
        return true;
    }

    private static boolean isLoggedIn(Map<String, String> requestHeaders) throws IOException {
        String cookieHeader = requestHeaders.get(COOKIE);
        if (cookieHeader == null) {
            return false;
        }

        String sessionId = parseCookies(cookieHeader).get(JSESSIONID);
        Session session = SessionManager.getInstance().findSession(sessionId);

        return session != null && session.getAttribute(USER) != null;
    }

    private static boolean handlePost(String path, Map<String, String> requestHeaders,
        BufferedReader bufferedReader, OutputStream outputStream) throws IOException {
        if (!path.equals(REGISTER_PATH) && !path.equals(LOGIN_PATH)) {
            return false;
        }

        Map<String, String> formData = parseFormData(readBody(requestHeaders, bufferedReader));
        if (path.equals(REGISTER_PATH)) {
            register(formData, outputStream);
            return true;
        }

        login(formData, outputStream);
        return true;
    }

    private static void register(Map<String, String> formData, OutputStream outputStream)
        throws IOException {
        User user = new User(
            formData.get("account"),
            formData.get("password"),
            formData.get("email"));

        InMemoryUserRepository.save(user);
        log.info("register user: {}", user);

        sendRedirect(outputStream, INDEX_PAGE);
    }

    private static void login(Map<String, String> formData, OutputStream outputStream)
        throws IOException {
        String password = formData.get("password");
        User user = InMemoryUserRepository.findByAccount(formData.get("account"))
            .orElse(null);

        if (user == null || !user.isMatchPassword(password)) {
            log.info("login failed: {}", formData.get("account"));
            sendResource(outputStream, UNAUTHORIZED_CODE + HTML_EXTENSION, UNAUTHORIZED_CODE);
            return;
        }

        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute(USER, user);
        SessionManager.getInstance().add(session);

        log.info("login success: {}, sessionId: {}", user, session.getId());
        sendRedirect(outputStream, INDEX_PAGE, session.getId());
    }

    private static String readBody(Map<String, String> requestHeaders,
        BufferedReader bufferedReader) throws IOException {
        String contentLength = requestHeaders.get(CONTENT_LENGTH);
        if (contentLength == null) {
            return "";
        }

        int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        int read = 0;
        while (read < length) {
            int count = bufferedReader.read(buffer, read, length - read);
            if (count == -1) {
                break;
            }
            read += count;
        }
        return new String(buffer, 0, read);
    }

    private static void sendResource(OutputStream outputStream, String fileName, String statusCode)
        throws IOException {
        URL resource = Http11Processor.class.getClassLoader()
            .getResource(RESOURCE_FILE_PREFIX + fileName);
        if (validateURLIsNull(resource, outputStream)) {
            return;
        }

        Path path = new File(resource.getPath()).toPath();
        byte[] body = Files.readAllBytes(path);

        writeAndFlush(outputStream, createResponse(fileName, body, path, statusCode).getBytes());
    }

    private static void sendRedirect(OutputStream outputStream, String location)
        throws IOException {
        sendRedirect(outputStream, location, null);
    }

    private static void sendRedirect(OutputStream outputStream, String location, String sessionId)
        throws IOException {
        StringJoiner response = new StringJoiner("\r\n");
        response.add("HTTP/1.1 " + REDIRECTION_FOUND_CODE + " " + FOUND_STATUS_RESPONSE + " ");
        response.add("Location: " + location + " ");
        response.add("Content-Length: 0 ");
        if (sessionId != null) {
            response.add("Set-Cookie: " + JSESSIONID + "=" + sessionId + "; Path=/; HttpOnly");
        }
        response.add("");
        response.add("");

        writeAndFlush(outputStream, response.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static Map<String, String> parseFormData(String formData) {
        return parseDelimited(formData, "&");
    }

    private static Map<String, String> parseCookies(String cookieHeader) {
        return parseDelimited(cookieHeader, ";");
    }

    private static Map<String, String> parseDelimited(String value, String delimiter) {
        Map<String, String> parsed = new HashMap<>();
        for (String pair : value.split(delimiter)) {
            String[] keyAndValue = pair.split("=", 2);
            if (keyAndValue.length != 2) {
                continue;
            }

            parsed.put(
                URLDecoder.decode(keyAndValue[0].strip(), StandardCharsets.UTF_8),
                URLDecoder.decode(keyAndValue[1].strip(), StandardCharsets.UTF_8));
        }
        return parsed;
    }

    private static void writeAndFlush(OutputStream outputStream, byte[] response)
        throws IOException {
        outputStream.write(response);
        outputStream.flush();
    }

    private static String createResponse(String part, byte[] body, Path path, String statusCode) throws IOException {
        String contentType = resolveContentType(part);
        if (contentType == null) {
            return "";
        }

        String statusResponse = SUCCESS_STATUS_RESPONSE;
        if (statusCode.equals(UNAUTHORIZED_CODE)) {
            statusResponse = UNAUTHORIZED_STATUS_RESPONSE;
        }
        if (statusCode.equals(REDIRECTION_FOUND_CODE)) {
            statusResponse = FOUND_STATUS_RESPONSE;
        }

        return String.join("\r\n",
            "HTTP/1.1 " + statusCode + " " + statusResponse + " ",
            "Content-Type: " + contentType + ";charset=utf-8 ",
            "Content-Length: " + body.length + " ",
            "",
            new String(Files.readAllBytes(path)));
    }

    private static String resolveContentType(String part) {
        if (part.endsWith(CSS_EXTENSION)) {
            return "text/css";
        }
        if (part.endsWith(JS_EXTENSION)) {
            return "text/javascript";
        }
        if (part.endsWith(HTML_EXTENSION) || part.equals("login") || part.equals("/login") || part.equals("/register")) {
            return "text/html";
        }

        return null;
    }

    private static boolean isRootRequest(String part, OutputStream outputStream)
        throws IOException {
        if (part.equals("/")) {
            final var responseBody = "Hello world!";

            final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

            writeAndFlush(outputStream, response.getBytes());
            return true;
        }
        return false;
    }

    private static boolean validateURLIsNull(URL resource, OutputStream outputStream)
        throws IOException {
        if (resource == null) {
            String notFound = String.join("\r\n",
                "HTTP/1.1 404 Not Found ",
                "Content-Length: 0 ",
                "",
                "");

            writeAndFlush(outputStream, notFound.getBytes(StandardCharsets.UTF_8));
            return true;
        }
        return false;
    }
}
