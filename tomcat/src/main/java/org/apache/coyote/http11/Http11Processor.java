package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.parser.HeaderParser;
import org.apache.coyote.http11.parser.QueryParamsParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    public static final String RESOURCE_DIRECTORY = "static";
    public static final String EXTENSION_SEPARATOR = ".";
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
             final var outputStream = connection.getOutputStream()) {

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
            Map<String, String> requestHeaders = HeaderParser.parse(bufferedReader);
            MimeType mimeType = resolveMimeType(requestLine, requestHeaders);

            if (requestLine.getMethod().equalsIgnoreCase("GET")) {
                HttpResponse response = buildResponse(requestLine, mimeType, requestHeaders);
                sendResponse(outputStream, response);
            }

            if (requestLine.getMethod().equalsIgnoreCase("POST")) {
                int contentLength = Integer.parseInt(requestHeaders.get("Content-Length"));
                char[] buffer = new char[contentLength];
                bufferedReader.read(buffer, 0, contentLength);
                String requestBody = new String(buffer);
                Map<String, String> queryParams = QueryParamsParser.parse(requestBody);
                HttpResponse httpResponse = dispatchRequest(requestLine.getPath(), queryParams, mimeType,
                        requestHeaders);
                sendResponse(outputStream, httpResponse);
            }

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private MimeType resolveMimeType(RequestLine requestLine, Map<String, String> requestHeaders) {
        String acceptHeaderValue = HeaderParser.extractPrimaryMimeType(requestHeaders);
        MimeType mimeType = MimeType.fromMimeTypeString(acceptHeaderValue);
        if (mimeType != null) {
            return mimeType;
        }
        return MimeType.fromExtensionString(requestLine.getExtension());
    }

    private HttpResponse buildResponse(RequestLine requestLine, MimeType mimeType, Map<String, String> requestHeaders)
            throws IOException {
        if (requestLine.isRootPath()) {
            return HttpResponse.of(HttpStatus.OK, mimeType, "Hello world!");
        }

        if (requestLine.hasQuery()) {
            Map<String, String> queryParams = QueryParamsParser.parse(requestLine.getQueryString());
            if (requestLine.getPath().equals("/login")) {
                return handleLogin(queryParams, mimeType, requestHeaders);
            }
        }

        if (requestLine.getPath().equals("/login") && isLoggedIn(requestHeaders)) {
            return redirectTo("/index.html", mimeType);
        }

        return serveStaticPath(requestLine, mimeType);
    }

    private HttpResponse serveStaticPath(RequestLine requestLine, MimeType mimeType) throws IOException {
        final Path filePath = getFilePath(requestLine, mimeType);
        if (filePath == null) {
            return HttpResponse.of(HttpStatus.NOT_FOUND, mimeType, "");
        }
        final String responseBody = Files.readString(filePath, StandardCharsets.UTF_8);
        return HttpResponse.of(HttpStatus.OK, mimeType, responseBody);
    }

    private HttpResponse dispatchRequest(String path, Map<String, String> queryParams, MimeType mimeType,
                                         Map<String, String> requestHeaders) throws IOException {
        if (path.equals("/login")) {
            return handleLogin(queryParams, mimeType, requestHeaders);
        }
        if (path.equals("/register")) {
            return handleRegister(queryParams, mimeType);
        }
        return HttpResponse.of(HttpStatus.NOT_FOUND, mimeType, "Not Found");
    }

    private Path getFilePath(RequestLine requestLine, MimeType mimeType) {
        String path = requestLine.getPath();
        if (requestLine.getExtension().isEmpty()) {
            path += EXTENSION_SEPARATOR + mimeType;
        }
        URL resource = getClass().getClassLoader().getResource(RESOURCE_DIRECTORY + path);
        if (resource == null) {
            return null;
        }
        return new File(resource.getFile()).toPath();
    }

    private HttpResponse handleLogin(Map<String, String> queryParams, MimeType mimeType,
                                     Map<String, String> requestHeaders) {
        String account = queryParams.get("account");
        String password = queryParams.get("password");
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> handleLoginSuccess(user, mimeType, requestHeaders))
                .orElseGet(() -> {
                    log.info("login failure: account= {}", account);
                    return redirectTo("/401.html", mimeType);
                });
    }

    private HttpResponse handleLoginSuccess(User user, MimeType mimeType, Map<String, String> requestHeaders) {
        log.info("login success: account= {}", user.getAccount());
        HttpResponse httpResponse = redirectTo("/index.html", mimeType);
        String jsessionid = getOrCreateJsessionId(requestHeaders, httpResponse);
        Session session = new Session(jsessionid);
        session.setAttribute("user", user);
        SessionManager.getInstance().add(session);
        return httpResponse;
    }

    private HttpResponse handleRegister(Map<String, String> queryParams, MimeType mimeType) {
        String account = queryParams.get("account");
        String password = queryParams.get("password");
        String email = queryParams.get("email");
        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("register success: account= {} email= {}", account, email);
        return redirectTo("/index.html", mimeType);
    }

    private HttpResponse redirectTo(String location, MimeType mimeType) {
        HttpResponse httpResponse = HttpResponse.of(HttpStatus.FOUND, mimeType, "");
        httpResponse.addHeader("Location", location);
        return httpResponse;
    }

    private String getOrCreateJsessionId(Map<String, String> requestHeaders, HttpResponse httpResponse) {
        String cookieHeader = requestHeaders.get("Cookie");
        Cookie cookie = Cookie.fromHeader(cookieHeader);
        String jsessionId = cookie.get("JSESSIONID");
        if (jsessionId == null) {
            jsessionId = UUID.randomUUID().toString();
            cookie.add("JSESSIONID", jsessionId);
            httpResponse.addHeader("Set-Cookie", "JSESSIONID=" + jsessionId);
        }
        return jsessionId;
    }

    private boolean isLoggedIn(Map<String, String> requestHeaders) {
        String cookieHeader = requestHeaders.get("Cookie");
        Cookie cookie = Cookie.fromHeader(cookieHeader);
        String jsessionId = cookie.get("JSESSIONID");
        Session session = SessionManager.getInstance().findSession(jsessionId);
        return session != null && session.getAttribute("user") != null;
    }

    private void sendResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        outputStream.write(response.toHttpResponseString().getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
