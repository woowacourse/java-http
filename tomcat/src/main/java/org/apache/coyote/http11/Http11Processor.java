package org.apache.coyote.http11;

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
    private static final String QUERY_STRING = "?";
    private static final String AND = "&";
    private static final String EQUAL = "=";
    private static final String REQUEST_LINE = "requestLine";

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

            var requestHeaders = findRequestHeaders(inputStream);
            var requestLine = requestHeaders.get(REQUEST_LINE);
            var parts = requestLine.split(" ");
            var method = parts[0];
            var uri = parts[1];
            int queryStartIndex = uri.indexOf(QUERY_STRING);
            String uriExceptQuery = findUriExceptQuery(queryStartIndex, uri);
            Map<String, String> queries = parseQueryString(queryStartIndex, uri);

            if (uri.startsWith("/login")) {
                String account = queries.get("account");
                String password = queries.get("password");
                UserService.login(account, password);
            }

            var status = 200;
            var reason = "OK";
            var responseBody = findResponseBody(uriExceptQuery);
            if (!responseBody.found()) {
                status = 404;
                reason = "NOT FOUND";
            }
            var contentType = findContentType(uriExceptQuery);

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

    private Map<String, String> findRequestHeaders(final InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        Map<String, String> headers = new HashMap<>();
        String requestLine = br.readLine();
        headers.put(REQUEST_LINE, requestLine);
        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            String[] split = line.split(":");
            headers.put(split[0], split[1]);
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

    private String findUriExceptQuery(final int queryStartIndex, final String uri) {
        if (queryStartIndex == -1) {
            return uri;
        }
        return uri.substring(0, queryStartIndex);
    }

    private Map<String, String> parseQueryString(final int queryStartIndex, final String uri) {
        if (!uri.contains(QUERY_STRING)) {
            return Map.of();
        }
        String query = uri.substring(queryStartIndex + 1);
        String[] splitByAnd = query.split(AND);
        Map<String, String> queries = new HashMap<>();
        for (String s : splitByAnd) {
            String[] splitByEqual = s.split(EQUAL);
            queries.put(splitByEqual[0], splitByEqual[1]);
        }
        return queries;
    }
}
