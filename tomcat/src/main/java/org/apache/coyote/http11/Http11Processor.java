package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final String ROOT_DIRECTORY = "/";
    private static final String DEFAULT_CONTENT_TYPE = "text/html";
    private static final String DEFAULT_BODY = "Hello world!";
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
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             final var outputStream = connection.getOutputStream()) {
            final String uri = bufferedReader.readLine()
                .split(" ")[1];
            final Map<String, String> target = parseTarget(uri);
            final String path = target.get("path");
            final Map<String, String> queryParams = extractQueryParams(target.get("queryString"));
            if (!queryParams.isEmpty()) {
                getUser(queryParams);
            }

            final String contentType = getContentType(path);
            final String body = readStaticResource(path);

            final String response = generateResponse(body, contentType);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> parseTarget(String uri) {
        final int index = uri.indexOf("?");
        String path = "";
        String queryString = "";
        if (index == -1) {
            path = uri;
            queryString = "";
        }
        if (index != -1) {
            path = uri.substring(0, index);
            queryString = uri.substring(index + 1);
        }
        if (!path.contains(".")) {
            path += ".html";
        }
        return Map.of(
            "path", path,
            "queryString", queryString);
    }

    private Map<String, String> extractQueryParams(final String queryString) {
        final Map<String, String> queryParams = new LinkedHashMap<>();
        if (queryString.isBlank()) {
            return queryParams;
        }
        Arrays.stream(queryString.split("&"))
            .map(keyValue -> keyValue.split("="))
            .forEach(splitted -> queryParams.put(splitted[0], splitted[1]));

        return queryParams;
    }

    private void getUser(Map<String, String> queryParams) {
        final String account = queryParams.get("account");
        final String password = queryParams.get("password");
        final User user = InMemoryUserRepository.findByAccount(account)
            .orElseThrow();
        if (user.checkPassword(password)) {
            log.info("user: {}", user);
        }
    }

    private String getContentType(final String filePath) {
        if (filePath.equals(ROOT_DIRECTORY)) {
            return DEFAULT_CONTENT_TYPE;
        }
        final String prefix = "text/";
        final int lastDotIndex = filePath.lastIndexOf(".");
        if (lastDotIndex == 0) {
            throw new IllegalArgumentException("유효한 타겟 uri가 아닙니다.");
        }
        return prefix + filePath.substring(lastDotIndex + 1);
    }

    private String readStaticResource(String filePath) throws IOException {
        if (filePath.equals(ROOT_DIRECTORY)) {
            return DEFAULT_BODY;
        }
        final StringBuffer readResource = new StringBuffer();

        final Path path = Path.of(getClass()
            .getResource("/static" + filePath)
            .getPath());

        String string;
        try (final BufferedReader bufferedReader =
            new BufferedReader(new FileReader(path.toFile()))) {
            while ((string = bufferedReader.readLine()) != null) {
                readResource.append(string);
                readResource.append("\n");
            }
        }

        return readResource.toString();
    }

    private String generateResponse(final String body, final String contentType)  {
        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: " + contentType + ";charset=utf-8 ",
            "Content-Length: " + body.getBytes().length + " ",
            "",
            body);
    }
}
