package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ROOT_RESOURCE_PATH = "/";
    private static final String STATIC_RESOURCE_PATH = "static";
    private static final String PATH_QUERY_SEPARATOR = "?";
    private static final String PATH_QUERY_SEPARATOR_REGEX = "//?";
    private static final String QUERY_PARAMETER_SEPARATOR = "&";
    private static final String QUERY_PARAMETER_NAME_VALUE_SEPARATOR = "=";
    private static final String REQUEST_LINE_ELEMENT_SEPARATOR = " ";

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
             final var outputStream = connection.getOutputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            final List<String> request = new ArrayList<>();

            while (true) {
                String currentLine = reader.readLine();
                if (currentLine == null || currentLine.isEmpty()) {
                    break;
                }
                request.add(currentLine);
            }

            final String requestLine = request.getFirst();

            final String target = requestLine.split(REQUEST_LINE_ELEMENT_SEPARATOR)[1];

            String targetPath = target;
            String targetQueryString = "";
            if (target.contains(PATH_QUERY_SEPARATOR)) {
                int delimiterIndex = target.indexOf(PATH_QUERY_SEPARATOR);
                targetPath = target.substring(0, delimiterIndex);
                targetQueryString = target.substring(delimiterIndex + PATH_QUERY_SEPARATOR.length());
            }

            String responseBody = "Hello world!";
            Path path;
            if (!targetPath.equals(ROOT_RESOURCE_PATH) && targetPath.contains(".")) {
                String resourceName = STATIC_RESOURCE_PATH + targetPath;
                URL resource = getClass().getClassLoader().getResource(resourceName);
                if (resource == null) {
                    log.info("Static resource not found: {}", targetPath);
                    writeNotFoundResponse(outputStream);
                    return;
                }
                try {
                    path = Path.of(resource.toURI());
                    responseBody = Files.readString(path);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }

            Map<String, String> maps = new HashMap<>();
            if (targetPath.equals("/login")) {
                List<String> queryParameters = Arrays.stream(targetQueryString.split(QUERY_PARAMETER_SEPARATOR))
                        .toList();

                for (String queryParameter : queryParameters) {
                    if (queryParameter.contains(QUERY_PARAMETER_NAME_VALUE_SEPARATOR)) {
                        maps.put(
                                queryParameter.substring(0,
                                        queryParameter.indexOf(QUERY_PARAMETER_NAME_VALUE_SEPARATOR)),
                                queryParameter.substring(queryParameter.indexOf(QUERY_PARAMETER_NAME_VALUE_SEPARATOR)
                                        + QUERY_PARAMETER_NAME_VALUE_SEPARATOR.length())
                        );
                    }
                }

                User account = InMemoryUserRepository.findByAccount(maps.get("account")).orElseThrow();
                if (account.checkPassword(maps.get("password"))) {
                    System.out.println(account.toString());
                    try {
                        responseBody = Files.readString(
                                Path.of(getClass().getClassLoader().getResource(STATIC_RESOURCE_PATH + "/login.html")
                                        .toURI()));
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            String contentType = "Content-Type: text/html;charset=utf-8 ";

            if (targetPath.contains(".")) {
                if (targetPath.split("\\.")[1].equals("html")) {
                    contentType = "Content-Type: text/html;charset=utf-8 ";
                }
                if (targetPath.split("\\.")[1].equals("css")) {
                    contentType = "Content-Type: text/css;charset=utf-8 ";
                }
                if (targetPath.split("\\.")[1].equals("js")) {
                    contentType = "Content-Type: text/js;charset=utf-8 ";
                }
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    contentType,
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody
            );

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void writeNotFoundResponse(final OutputStream outputStream) throws IOException {
        final String response = String.join("\r\n",
                "HTTP/1.1 404 Not Found",
                "Content-Length: 0",
                "Connection: close",
                "",
                "");
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
