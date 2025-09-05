package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final String RESOURCE_PATH = "static";
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
             final var outputStream = connection.getOutputStream())
        {
            final Http11Request http11Request = new Http11Request(extractRequestHeaders(inputStream));
            final Http11Response http11Response = handleRequest(http11Request);

            outputStream.write(http11Response.toBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Http11Response handleRequest(final Http11Request request) throws URISyntaxException, IOException {
        final String uri = request.getUri();
        final String path = extractPath(uri);
        final URL url = getURL(path);
        final String contentType = extractContentType(path);
        final String responseBody;

        if (uri.equals("/")) {
            responseBody = "Hello world!";
            return Http11Response.ok(contentType, responseBody);
        }

        if (url == null) {
            responseBody = Files.readString(Paths.get(getClass().getClassLoader().getResource("static/404.html").toURI()));
            return Http11Response.notFound(contentType, responseBody);
        }

        responseBody = Files.readString(Paths.get(url.toURI()));
        return Http11Response.ok(contentType, responseBody);
    }

    private List<String> extractRequestHeaders(final InputStream inputStream) throws IOException{
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        List<String> requestHeaders = new ArrayList<>();
        String requestLine;
        while ((requestLine = bufferedReader.readLine()) != null && !requestLine.isEmpty()) {
            requestHeaders.add(requestLine);
        }

        return requestHeaders;
    }

    private void parseQueryString(final String uri) {
        final String query = extractQuery(uri);
        if (query == null) {
            return;
        }

        Map<String, String> params = new HashMap<>();
        String[] pairs = query.split("&");

        for(String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }

        String account = params.get("account");
        String password = params.get("password");

        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresentOrElse(
                        user -> log.info("user : {}", user),
                        () -> log.warn("계정과 비밀번호에 해당하는 user가 존재하지 않습니다.")
                );
    }

    private URL getURL(final String path) {
        return getClass().getClassLoader().getResource(path);
    }

    private String extractPath(final String uri) {
        int index = uri.indexOf("?");
        if (index == -1) {
            return RESOURCE_PATH + uri;
        }

        parseQueryString(uri);
        return RESOURCE_PATH + uri.substring(0, index) + ".html";
    }

    private String extractQuery(final String uri) {
        int index = uri.indexOf("?");
        if (index == -1) {
            return null;
        }

        return uri.substring(index + 1);
    }

    private String extractContentType(final String requestPath) {
        if (requestPath.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }

        return "text/html;charset=utf-8";
    }
}
