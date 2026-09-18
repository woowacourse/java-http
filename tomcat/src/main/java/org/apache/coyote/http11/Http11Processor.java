package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String CRLF = "\r\n";
    private static final String STATIC_RESOURCE_ROOT = "static";
    private static final String NOT_FOUND_PAGE = "/404.html";
    private static final String LOGIN_PATH = "/login";
    private static final String ACCOUNT_PARAMETER = "account";

    private static final String ACCEPT_HEADER = "Accept";
    private static final String ACCEPT_ANY = "*/*";
    private static final String TEXT_HTML = "text/html;charset=utf-8";
    private static final String TEXT_CSS = "text/css";

    private static final String STATUS_OK = "HTTP/1.1 200 OK ";
    private static final String STATUS_NOT_FOUND = "HTTP/1.1 404 Not Found ";

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
             final var inputStreamReader = new InputStreamReader(inputStream);
             final var bufferedReader = new BufferedReader(inputStreamReader);
             final var outputStream = connection.getOutputStream()) {

            final var requestLine = readRequestLine(bufferedReader);
            if (requestLine == null) {
                return;
            }
            final var requestHeaders = readHeaders(bufferedReader);
            final var contentType = decideContentType(requestHeaders);
            final var uri = parseUri(requestLine);
            final var pathAndQueryString = splitPathAndQueryString(uri);
            final var path = pathAndQueryString[0];
            final var queryParameters = parseQueryParameters(pathAndQueryString);
            logLoginUser(path, queryParameters);

            var statusLine = STATUS_OK;
            final byte[] responseBody;
            if (path.equals("/")) {
                responseBody = "Hello world!".getBytes();
            } else {
                var resourceUrl = findResource(path);
                if (resourceUrl == null) {
                    statusLine = STATUS_NOT_FOUND;
                    resourceUrl = findResource(NOT_FOUND_PAGE);
                }
                log.info("{} -> {}", statusLine, resourceUrl);
                responseBody = Files.readAllBytes(Path.of(resourceUrl.toURI()));
            }

            final var response = buildResponse(statusLine, contentType, responseBody);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String readRequestLine(final BufferedReader reader) throws IOException {
        final var requestLine = reader.readLine();
        log.info("requestLine: {}", requestLine);
        return requestLine;
    }

    private Map<String, String> readHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> headers = new LinkedHashMap<>();
        String headerLine;
        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
            final String[] nameAndValue = headerLine.split(":", 2);
            headers.put(nameAndValue[0], nameAndValue[1].trim());
        }
        log.info("headers: {}", headers);
        return headers;
    }

    private String decideContentType(final Map<String, String> requestHeaders) {
        final var accept = requestHeaders.getOrDefault(ACCEPT_HEADER, ACCEPT_ANY);
        if (accept.contains(TEXT_CSS)) {
            return TEXT_CSS;
        }
        return TEXT_HTML;
    }

    private String parseUri(final String requestLine) {
        final String[] tokens = requestLine.split(" ");
        return tokens[1];
    }

    private String[] splitPathAndQueryString(final String uri) {
        return uri.split("\\?", 2);
    }

    private Map<String, String> parseQueryParameters(final String[] pathAndQueryString) {
        final Map<String, String> queryParameters = new LinkedHashMap<>();
        if (pathAndQueryString.length < 2) {
            return queryParameters;
        }
        final String[] pairs = pathAndQueryString[1].split("&");
        for (int i = 0; i < pairs.length; i++) {
            final String[] nameAndValue = pairs[i].split("=", 2);
            queryParameters.put(URLDecoder.decode(nameAndValue[0], StandardCharsets.UTF_8),
                    URLDecoder.decode(nameAndValue[1], StandardCharsets.UTF_8));
        }
        return queryParameters;
    }

    private void logLoginUser(final String path, final Map<String, String> queryParameters) {
        if (path.equals(LOGIN_PATH) && queryParameters.containsKey(ACCOUNT_PARAMETER)) {
            InMemoryUserRepository.findByAccount(queryParameters.get(ACCOUNT_PARAMETER))
                    .ifPresent(user -> log.info("user: {}", user));
        }
    }

    private URL findResource(final String path) {
        if (path.contains(".")) {
            return getClass().getClassLoader().getResource(STATIC_RESOURCE_ROOT + path);
        }
        return getClass().getClassLoader().getResource(STATIC_RESOURCE_ROOT + path + ".html");
    }

    private String buildResponse(final String statusLine, final String contentType, final byte[] body) {
        return String.join(CRLF,
                statusLine,
                "Content-Type: " + contentType + " ",
                "Content-Length: " + body.length + " ",
                "",
                new String(body, StandardCharsets.UTF_8));
    }
}
