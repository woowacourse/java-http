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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.parser.HeaderParser;
import org.apache.coyote.http11.parser.QueryParamsParser;
import org.apache.coyote.http11.parser.UriParser;
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

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
            Map<String, String> requestHeaders = HeaderParser.parse(bufferedReader);
            MimeType mimeType = resolveMimeType(requestLine.getPath(), requestHeaders);

            if (requestLine.getMethod().equalsIgnoreCase("GET")) {
                HttpResponse response = buildResponse(requestLine.getPath(), mimeType, requestHeaders);
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

    private MimeType resolveMimeType(String requestUri, Map<String, String> requestHeaders) {
        String acceptHeaderValue = HeaderParser.extractPrimaryMimeType(requestHeaders);
        MimeType mimeType = MimeType.fromMimeTypeString(acceptHeaderValue);

        if (mimeType != null) {
            return mimeType;
        }

        if (UriParser.hasQuery(requestUri)) {
            requestUri = UriParser.extractPath(requestUri);
        }
        String extension = UriParser.extractExtension(requestUri);
        return MimeType.fromExtensionString(extension);
    }

    private HttpResponse buildResponse(String requestUri, MimeType mimeType, Map<String, String> requestHeaders)
            throws IOException {
        if (UriParser.isRootPath(requestUri)) {
            return HttpResponse.of(HttpStatus.OK, mimeType, "Hello world!");
        }

        String path = requestUri;
        if (UriParser.hasQuery(requestUri)) {
            path = UriParser.extractPath(requestUri);
            String queryString = UriParser.extractQueryString(requestUri);
            Map<String, String> queryParams = QueryParamsParser.parse(queryString);
            if (path.equals("/login")) {
                return handleLogin(queryParams, mimeType, requestHeaders);
            }
        }

        Path filePath = getFilePath(path, mimeType);
        String responseBody = new String(Files.readAllBytes(filePath));
        return HttpResponse.of(HttpStatus.OK, mimeType, responseBody);
    }

    private HttpResponse dispatchRequest(String path, Map<String, String> queryParams, MimeType mimeType,
                                         Map<String, String> requestHeaders) {
        if (path.equals("/login")) {
            return handleLogin(queryParams, mimeType, requestHeaders);
        }
        if (path.equals("/register")) {
            return handleRegister(queryParams, mimeType);
        }
        return null;
    }

    private Path getFilePath(String path, MimeType mimeType) {
        if (UriParser.extractExtension(path).isEmpty()) {
            path += EXTENSION_SEPARATOR + mimeType;
        }
        URL resource = getClass().getClassLoader().getResource(RESOURCE_DIRECTORY + path);
        return new File(resource.getFile()).toPath();
    }

    private HttpResponse handleLogin(Map<String, String> queryParams, MimeType mimeType,
                                     Map<String, String> requestHeaders) {
        String account = queryParams.get("account");
        String password = queryParams.get("password");
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> handleLoginSuccess(account, mimeType, requestHeaders))
                .orElseGet(() -> {
                    log.info("login failure: account= {}", account);
                    return redirectTo("/401.html", mimeType);
                });
    }

    private HttpResponse handleLoginSuccess(String account, MimeType mimeType, Map<String, String> requestHeaders) {
        log.info("login success: account= {}", account);
        HttpResponse httpResponse = redirectTo("/index.html", mimeType);
        handleCookie(requestHeaders, httpResponse);
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

    private void handleCookie(Map<String, String> requestHeaders, HttpResponse httpResponse) {
        String cookieHeader = requestHeaders.get("Cookie");
        Cookie cookie = Cookie.fromHeader(cookieHeader);

        String jsessionId = cookie.get("JSESSIONID");

        if (jsessionId == null) {
            jsessionId = UUID.randomUUID().toString();
            cookie.add("JSESSIONID", jsessionId);
            httpResponse.addHeader("Set-Cookie", "JSESSIONID=" + jsessionId);
        }
    }

    private void sendResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        outputStream.write(response.toHttpResponseString().getBytes());
        outputStream.flush();
    }
}
