package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.cookie.HttpCookie;
import org.apache.coyote.Processor;
import org.apache.session.Session;
import org.apache.session.SessionManager;
import org.apache.session.SessionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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
             final var outputStream = connection.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            String headerFirstLine = bufferedReader.readLine();

            Map<String, String> headers = readHeaders(bufferedReader);
            int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
            String reqBody = readReqBody(bufferedReader, contentLength);

            String method = headerFirstLine.split(" ")[0];
            String reqUri = headerFirstLine.split(" ")[1];

            int queryIndex = reqUri.indexOf("?");
            String pathUri = readPathUri(reqUri, queryIndex);
            String query = readQuery(reqUri, queryIndex);

            if (pathUri.equals("/register") && method.equals("POST")) {
                handleRegister(reqBody, outputStream);
                return;
            }

            if (pathUri.equals("/login") && method.equals("GET")) {
                if (isLogin(headers.get("Cookie"))) {
                    writeAndFlush(outputStream, createRedirectResponse());
                    return;
                }
            }

            if (pathUri.equals("/login") && method.equals("POST")) {
                User user = login(reqBody);

                if (user != null) {
                    SessionResult sessionResult = storeSession(headers, user);
                    writeAndFlush(
                            outputStream,
                            createLoginResponse(sessionResult.getJsessionId(), sessionResult.isNew())
                    );
                    return;
                }

                pathUri = "/401.html";
            }

            pathUri = normalizePathUri(pathUri);
            Path path = getPath("static" + pathUri);

            String responseBody = findResponseBody(pathUri, path);
            String contentType = extractType(path);
            writeAndFlush(outputStream, createStaticFileResponse(responseBody, contentType));

        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static SessionResult storeSession(Map<String, String> headers, User user) {
        String jsessionId = getOrCreateJsessionId(headers.get("Cookie"));
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = sessionManager.findSession(jsessionId);

        boolean isNew = false;

        if (session == null) {
            session = new Session(jsessionId);
            sessionManager.add(session);
            isNew = true;
        }

        session.setAttribute("user", user);
        return new SessionResult(jsessionId, isNew);
    }

    private static void handleRegister(String reqBody, OutputStream outputStream) throws IOException {
        Map<String, String> reqBodyParams = parseQueryParams(reqBody);

        InMemoryUserRepository.save(new User(
                reqBodyParams.get("account"),
                reqBodyParams.get("password"),
                reqBodyParams.get(("email"))
        ));

        writeAndFlush(outputStream, createRedirectResponse());
    }

    private static User login(String reqBody) {
        Map<String, String> queryParams = parseQueryParams(reqBody);

        if (queryParams.get("account") == null || queryParams.get("password") == null) {
            return null;
        }

        return InMemoryUserRepository.findByAccount(queryParams.get("account"))
                .filter(user -> user.checkPassword(queryParams.get("password")))
                .orElse(null);
    }

    private static String findResponseBody(String pathUri, Path path) throws IOException {
        if (pathUri.equals("/")) {
            return "Hello world!";
        }

        return Files.readString(path, StandardCharsets.UTF_8);
    }

    private static void writeAndFlush(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private static String createStaticFileResponse(String responseBody, String type) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/" + type + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                responseBody);
    }

    private static String createLoginResponse(String jsessionId, boolean isNew) {
        StringBuilder response = new StringBuilder()
                .append("HTTP/1.1 302 Found\r\n");

        if (isNew) {
            response.append("Set-Cookie: JSESSIONID=")
                    .append(jsessionId)
                    .append("\r\n");
        }

        response.append("Location: /index.html\r\n")
                .append("Content-Length: 0\r\n")
                .append("\r\n");

        return response.toString();
    }

    private static String getOrCreateJsessionId(String cookie) {
        HttpCookie httpCookie = new HttpCookie();
        httpCookie.parseCookie(cookie);
        String jsessionid = httpCookie.getCookieValue("JSESSIONID");

        if (jsessionid == null || jsessionid.isBlank()) {
            return httpCookie.generateCookie();
        }
        return jsessionid;
    }

    private static String createRedirectResponse() {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index.html",
                "Content-Length: 0",
                "",
                "");
    }

    private static String extractType(Path path) {
        if (path.toString().endsWith(".css")) {
            return "css";
        }
        if (path.toString().endsWith(".js")) {
            return "javascript";
        }
        return "html";
    }

    private static Map<String, String> parseQueryParams(String queryParams) {
        Map<String, String> queries = new HashMap<>();

        for (String s : queryParams.split("&")) {
            queries.put(s.split("=")[0], s.split("=")[1]);
        }

        return queries;
    }

    private static Path getPath(String filePath) throws URISyntaxException {
        URL resource = Objects.requireNonNull(
                Http11Processor.class.getClassLoader().getResource(filePath),
                "리소스 못찾음"
        );

        return Path.of(resource.toURI());
    }

    private static String readReqBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        if (contentLength == 0) {
            return "";
        }

        char[] body = new char[contentLength];
        int offset = 0;

        while (offset < contentLength) {
            int read = bufferedReader.read(body, offset, contentLength - offset);

            if (read == -1) {
                throw new IOException("요청 바디가 중간에 끝났습니다.");
            }
            offset += read;
        }
        return new String(body);
    }

    private boolean isLogin(String cookie) {
        HttpCookie httpCookie = new HttpCookie();
        httpCookie.parseCookie(cookie);
        String jsessionId = httpCookie.getCookieValue("JSESSIONID");

        if (jsessionId == null || jsessionId.isBlank()) {
            return false;
        }
        Session session = SessionManager.getInstance().findSession(jsessionId);

        return session != null && session.getAttribute("user") != null;
    }

    private Map<String, String> readHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headerMaps = new HashMap<>();
        String line;

        while (!(line = bufferedReader.readLine()).isEmpty()) {
            headerMaps.put(line.split(":")[0].trim(), line.split(":")[1].trim());
        }

        return headerMaps;
    }

    private String readQuery(String reqUri, int queryIndex) {
        if (queryIndex == -1) {
            return "";
        }
        return reqUri.substring(queryIndex + 1);
    }

    private String readPathUri(String reqUri, int queryIndex) {
        if (queryIndex == -1) {
            return reqUri;
        }
        return reqUri.substring(0, queryIndex);
    }

    private String normalizePathUri(String pathUri) {
        if (pathUri.equals("/")) {
            return "/";
        }
        if (!pathUri.contains(".")) {
            return pathUri + ".html";
        }
        return pathUri;
    }
}
