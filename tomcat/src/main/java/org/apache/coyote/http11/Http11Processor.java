package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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
             final var outputStream = connection.getOutputStream()) {

            HttpRequest request = getRequest(inputStream);

            String requestUri = request.getUri();
            String body = getBody(requestUri);

            HttpResponse response = getResponse(requestUri, body);

            if (requestUri.startsWith("/login")) {
                String account = request.getQueryParameter("account");
                User user = InMemoryUserRepository.findByAccount(account).orElse(null);
                if (user != null) {
                    log.info("user: {}", user);
                }
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest getRequest(InputStream inputStream) throws IOException {

        Map<String, String> httpHeader = new HashMap<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isBlank()) {
            throw new IOException("request is empty");
        }

        String[] requestLineSplit = requestLine.split(" ");

        String method = requestLineSplit[0];
        httpHeader.put("method", method);

        String uri = requestLineSplit[1];
        if (uri.contains("?")) {
            int index = uri.indexOf("?");
            uri = uri.substring(0, index + 1);
            httpHeader.put("request-uri", uri);
        } else {
            httpHeader.put("request-uri", uri);
        }

        String version = requestLineSplit[2];
        httpHeader.put("http-version", version);

        String line;
        while(!"".equals((line = reader.readLine()))) {
            String[] split = line.split(":");
            String fieldName = split[0];
            String value = split[1];
            httpHeader.put(fieldName, value);
        }

        QueryParameter queryParameter = parseQueryParameter(uri);

        return new HttpRequest(httpHeader, queryParameter);
    }

    private String getBody(String uri) throws IOException {
        if ("/".equals(uri)) {
            return "Hello world!";
        }

        String resourcePath;
        if (uri.contains(".")) {
            resourcePath = "./static" + uri;
        } else {
            resourcePath = "./static" + uri + ".html";
        }

        URL systemResource = ClassLoader.getSystemResource(resourcePath);
        Path path = Path.of(systemResource.getPath());
        final var responseBody = Files.readAllBytes(path);

        return new String(responseBody);
    }

    private HttpResponse getResponse(String uri, String body) throws IOException {
        String extension = "html";
        String contentType = String.join("/","text",extension);

        if (uri.endsWith(".css") || uri.endsWith(".html")) {
             extension = uri.substring(uri.lastIndexOf('.') + 1);
             contentType = String.join("/","text",extension);
        }

        if (uri.endsWith(".js")) {
             contentType = String.join("/","application","javascript");
        }

        return new HttpResponse("200 OK",  contentType, body);
    }

    private QueryParameter parseQueryParameter(String uri) {
        if (uri.contains("?")) {
            int index = uri.indexOf("?");
            String queryString = uri.substring(index + 1);

            Map<String, String> queryParameters = new HashMap<>();
            String[] parameters = queryString.split("&");
            for (String parameter : parameters) {
                String key = parameter.split("=")[0];
                String value = parameter.split("=")[1];
                queryParameters.put(key, value);
            }
        return new QueryParameter(queryParameters);
        }

        return new QueryParameter();
    }
}
