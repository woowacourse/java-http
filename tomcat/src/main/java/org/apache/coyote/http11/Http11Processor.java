package org.apache.coyote.http11;

import com.techcourse.exception.BadRequestException;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.service.UserService;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.apache.coyote.dto.ResourceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String CRLF = "\r\n";
    private static final String QUESTION = "?";
    private static final String AND = "&";
    private static final String EQUAL = "=";
    private static final String COLON = ":";

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

            HttpRequest requestHeaders = readHttpRequest(inputStream);
            RequestLine requestLine = requestHeaders.requestLine();
            String method = requestLine.method();
            String uri = requestLine.uri();

            int questionIndex = uri.indexOf(QUESTION);
            String path = findPath(questionIndex, uri);

            Map<String, String> queries = findQueries(questionIndex, uri);

            if (uri.startsWith("/login")) {
                String account = queries.get("account");
                String password = queries.get("password");
                UserService.login(account, password);
            }

            var status = 200;
            var reason = "OK";
            var responseBody = findResponseBody(path);
            if (!responseBody.found()) {
                status = 404;
                reason = "NOT FOUND";
            }
            var contentType = findContentType(path);

            final var response = String.join(CRLF,
                    "HTTP/1.1 " + status + " " + reason,
                    "Content-Type: " + contentType,
                    "Content-Length: " + responseBody.body().getBytes().length,
                    "",
                    responseBody.body());

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readHttpRequest(final InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        RequestLine requestLine = readRequestLine(br);
        Map<String, String> headers = readHeaders(br);

        byte[] body = new byte[0];
        String len = headers.get("content-length");
        if (len != null) {
            int contentLength = Integer.parseInt(len);
            body = inputStream.readNBytes(contentLength);
        }
        return new HttpRequest(requestLine, headers, body);
    }

    private RequestLine readRequestLine(final BufferedReader br) throws IOException {
        String requestLine = br.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new BadRequestException("Request line is empty");
        }

        String[] parts = requestLine.split(" ");
        String method = parts[0];
        String uri = parts[1];
        String version = parts[2];
        return new RequestLine(method, uri, version);
    }

    private Map<String, String> readHeaders(final BufferedReader br) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            int colon = line.indexOf(COLON);
            if (colon < 0) {
                throw new BadRequestException("Header is malformed");
            }
            String name = line.substring(0, colon).toLowerCase();
            String value = line.substring(colon + 1);
            headers.put(name, value);
        }
        return headers;
    }

    private ResourceResult findResponseBody(String uri) throws IOException {
        if (uri.equals("/")) {
            return ResourceResult.found("Hello world!");
        }
        if (!uri.contains(".")) {
            uri += ".html";
        }
        final URL resource = getClass().getClassLoader().getResource("static/" + uri);
        if (resource == null) {
            return ResourceResult.notFound();
        }
        String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        return ResourceResult.found(content);
    }

    private String findContentType(final String uri) {
        String encoding = ";charset=utf-8";
        if (uri.endsWith(".html")) {
            return "text/html" + encoding;
        }
        if (uri.endsWith(".css")) {
            return "text/css" + encoding;
        }
        if (uri.endsWith(".js")) {
            return "application/javascript" + encoding;
        }
        if (uri.endsWith(".png")) {
            return "image/png";
        }
        return "";
    }

    private Map<String, String> parseQueryString(final int queryStartIndex, final String uri) {
        if (!uri.contains(QUESTION)) {
            return Map.of();
        }
        String query = uri.substring(queryStartIndex + 1);
        String[] pairs = query.split(AND);
        Map<String, String> queries = new HashMap<>();
        for (String pair : pairs) {
            int equalIndex = pair.indexOf(EQUAL);
            String name = pair.substring(0, equalIndex);
            String value = pair.substring(equalIndex + 1);
            queries.put(name, value);
        }
        return queries;
    }

    private String findPath(final int queryStartIndex, final String uri) {
        if (queryStartIndex < 0) {
            return uri;
        }
        return uri.substring(0, queryStartIndex);
    }

    private Map<String, String> findQueries(final int questionIndex, final String uri) {
        if (questionIndex > 0) {
            String rawQueries = uri.substring(questionIndex + 1);
            return parseQueryString(questionIndex, rawQueries);
        }
        return Map.of();
    }
}
