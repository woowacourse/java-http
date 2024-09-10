package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapper requestMapper;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestMapper = new RequestMapper();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {
            HttpRequest request = parseInput(inputStream);

            String resourceName = requestMapper.requestMapping(request);
            String responseBody = getResource(resourceName);
            String resourceExtension = getExtension(resourceName);

            HttpResponse response = new HttpResponse(responseBody, resourceExtension);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest parseInput(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String tokens[] = bufferedReader.readLine()
                .split(" ");
        if (tokens.length != 3) {
            throw new RuntimeException();
        }

        String path = parsePath(tokens[1]);
        Map<String, String> queryString = parseQueryString(tokens[1]);
        Map<String, String> headers = parseHeaders(bufferedReader);
        return new HttpRequest(tokens[0], path, queryString, tokens[2], headers);
    }

    private String parsePath(String token) {
        int separatorIndex = token.indexOf('?');
        if (separatorIndex == -1) {
            return token;
        }
        return token.substring(0, separatorIndex);
    }

    private Map<String, String> parseQueryString(String token) {
        Map<String, String> queryString = new HashMap<>();
        int separatorIndex = token.indexOf('?');
        if (separatorIndex == -1) {
            return queryString;
        }
        Stream.of(token.substring(separatorIndex + 1)
                        .split("&"))
                .forEach(data -> parseData(data, queryString));
        return queryString;
    }

    private void parseData(String s, Map<String, String> queryString) {
        String data[] = s.split("=");
        if (data.length == 2) {
            queryString.put(data[0], data[1]);
        }
    }

    private String getExtension(String resourceName) {
        int extensionIndex = resourceName.indexOf('.') + 1;
        if (extensionIndex == 0) {
            return "html";
        }
        return resourceName.substring(extensionIndex);
    }

    private String getResource(String resourceName) throws URISyntaxException, IOException {
        URL resource = getClass().getClassLoader()
                .getResource("static" + resourceName);

        try {
            return Files.readString(Paths.get(resource.toURI()));
        } catch (IOException e) {
            return "Hello world!";
        }
    }

    private Map<String, String> parseHeaders(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        Map<String, String> headers = new LinkedHashMap<>();

        while (!"".equals(line)) {
            if (line == null) {
                return headers;
            }
            int index = line.indexOf(':');
            String key = line.substring(0, index);
            String value = line.substring(index + 2);
            headers.put(key, value);
            line = bufferedReader.readLine();
        }
        return headers;
    }
}
