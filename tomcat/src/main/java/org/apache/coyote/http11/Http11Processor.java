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

            String requestUri = getRequestUri(bufferedReader);
            Map<String, String> requestHeaders = HeaderParser.parse(bufferedReader);

            String contentType = resolveContentType(requestUri, requestHeaders);
            HttpResponse response = buildResponse(requestUri, contentType);

            sendResponse(outputStream, response);

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getRequestUri(BufferedReader bufferedReader) throws IOException {
        String startLine = bufferedReader.readLine();
        return startLine.split(" ")[1];
    }

    private String resolveContentType(String requestUri, Map<String, String> requestHeaders) {
        String contentType = HeaderParser.extractPrimaryContentType(requestHeaders);
        if (!contentType.isEmpty()) {
            return contentType;
        }

        if (UriParser.hasQuery(requestUri)) {
            requestUri = UriParser.extractPath(requestUri);
        }
        String extension = UriParser.extractExtension(requestUri);
        if (!extension.isEmpty()) {
            return extension;
        }

        return "html";
    }

    private HttpResponse buildResponse(String requestUri, String contentType) throws IOException {
        if (UriParser.isRootPath(requestUri)) {
            return HttpResponse.of(HttpStatus.OK, contentType, "Hello world!");
        }

        String path = requestUri;
        if (UriParser.hasQuery(requestUri)) {
            path = UriParser.extractPath(requestUri);
            String queryString = UriParser.extractQueryString(requestUri);
            Map<String, String> queryParams = QueryParamsParser.parse(queryString);
            if (path.equals("/login")) {
                return handleLogin(queryParams, contentType);
            }
        }

        Path filePath = getFilePath(path, contentType);
        String responseBody = new String(Files.readAllBytes(filePath));
        return HttpResponse.of(HttpStatus.OK, contentType, responseBody);
    }

    private Path getFilePath(String path, String contentType) {
        if (UriParser.extractExtension(path).isEmpty()) {
            path += EXTENSION_SEPARATOR + contentType;
        }
        URL resource = getClass().getClassLoader().getResource(RESOURCE_DIRECTORY + path);
        return new File(resource.getFile()).toPath();
    }

    private HttpResponse handleLogin(Map<String, String> queryParams, String contentType) {
        final String account = queryParams.get("account");
        final String password = queryParams.get("password");
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> handleSuccess(user, contentType))
                .orElseGet(() -> handleFailure(account, contentType));
    }

    private HttpResponse handleSuccess(User user, String contentType) {
        log.info("login success: {}", user);
        HttpResponse httpResponse = HttpResponse.of(HttpStatus.FOUND, contentType, "");
        httpResponse.addHeader("Location", "/index.html");
        return httpResponse;
    }

    private HttpResponse handleFailure(String account, String contentType) {
        log.info("login failure: account= {}", account);
        HttpResponse httpResponse = HttpResponse.of(HttpStatus.FOUND, contentType, "");
        httpResponse.addHeader("Location", "/401.html");
        return httpResponse;
    }

    private void sendResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        outputStream.write(response.toHttpResponseString().getBytes());
        outputStream.flush();
    }
}
