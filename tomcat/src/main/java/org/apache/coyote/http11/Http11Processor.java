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
import java.util.Optional;
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

            if (UriParser.isRootPath(requestUri)) {
                String response = buildResponse("Hello world!", contentType);
                sendResponse(outputStream, response);
                return;
            }

            String path = requestUri;
            if (UriParser.hasQuery(requestUri)) {
                String queryString = UriParser.extractQueryString(requestUri);
                Map<String, String> queryParams = QueryParamsParser.parse(queryString);
                path = UriParser.extractPath(requestUri);
                findUserByAccountParam(queryParams);
            }

            Path filePath = getFilePath(path, contentType);
            String responseBody = new String(Files.readAllBytes(filePath));
            String response = buildResponse(responseBody, contentType);
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

        String extension = UriParser.extractExtension(requestUri);
        if (!extension.isEmpty()) {
            return extension;
        }

        return "html";
    }

    private Path getFilePath(String path, String contentType) {
        if (UriParser.extractExtension(path).isEmpty()) {
            path += EXTENSION_SEPARATOR + contentType;
        }
        URL resource = getClass().getClassLoader().getResource(RESOURCE_DIRECTORY + path);
        return new File(resource.getFile()).toPath();
    }

    private void findUserByAccountParam(Map<String, String> queryParams) {
        Optional<User> user = InMemoryUserRepository.findByAccount(queryParams.get("account"));
        System.out.println("user = " + user);
    }

    private String buildResponse(String responseBody, String contentType) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + "text/" + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private void sendResponse(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
