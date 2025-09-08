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

            MimeType mimeType = resolveMimeType(requestUri, requestHeaders);
            HttpResponse response = buildResponse(requestUri, mimeType);

            sendResponse(outputStream, response);

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getRequestUri(BufferedReader bufferedReader) throws IOException {
        String startLine = bufferedReader.readLine();
        return startLine.split(" ")[1];
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

    private HttpResponse buildResponse(String requestUri, MimeType mimeType) throws IOException {
        if (UriParser.isRootPath(requestUri)) {
            return HttpResponse.of(HttpStatus.OK, mimeType, "Hello world!");
        }

        String path = requestUri;
        if (UriParser.hasQuery(requestUri)) {
            path = UriParser.extractPath(requestUri);
            String queryString = UriParser.extractQueryString(requestUri);
            Map<String, String> queryParams = QueryParamsParser.parse(queryString);
            if (path.equals("/login")) {
                return handleLogin(queryParams, mimeType);
            }
        }

        Path filePath = getFilePath(path, mimeType);
        String responseBody = new String(Files.readAllBytes(filePath));
        return HttpResponse.of(HttpStatus.OK, mimeType, responseBody);
    }

    private Path getFilePath(String path, MimeType mimeType) {
        if (UriParser.extractExtension(path).isEmpty()) {
            path += EXTENSION_SEPARATOR + mimeType;
        }
        URL resource = getClass().getClassLoader().getResource(RESOURCE_DIRECTORY + path);
        return new File(resource.getFile()).toPath();
    }

    private HttpResponse handleLogin(Map<String, String> queryParams, MimeType mimeType) {
        final String account = queryParams.get("account");
        final String password = queryParams.get("password");
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> handleSuccess(user, mimeType))
                .orElseGet(() -> handleFailure(account, mimeType));
    }

    private HttpResponse handleSuccess(User user, MimeType mimeType) {
        log.info("login success: {}", user);
        HttpResponse httpResponse = HttpResponse.of(HttpStatus.FOUND, mimeType, "");
        httpResponse.addHeader("Location", "/index.html");
        return httpResponse;
    }

    private HttpResponse handleFailure(String account, MimeType mimeType) {
        log.info("login failure: account= {}", account);
        HttpResponse httpResponse = HttpResponse.of(HttpStatus.FOUND, mimeType, "");
        httpResponse.addHeader("Location", "/401.html");
        return httpResponse;
    }

    private void sendResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        outputStream.write(response.toHttpResponseString().getBytes());
        outputStream.flush();
    }
}
