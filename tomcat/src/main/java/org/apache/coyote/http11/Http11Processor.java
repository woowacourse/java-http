package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String INDEX_URI = "/index.html";
    private static final String STYLES_CSS = "/css/styles.css";
    private static final String RESOURCES_PREFIX = "static";

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    private static String getUri(String line) {
        return line.split(" ")[1];
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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            String method = line.split(" ")[0];
            String path = getPath(line);

            final var responseBody = getResponseBody(method, line);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/" + getExtension(path) + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getPath(String line) {
        String uri = getUri(line);

        return uri.split("'?'")[0];
    }

    private Map<String, String> getParameters(String line) {
        String uri = getUri(line);
        String queryString = uri.split("'?'")[1];

        String[] splitQuery = queryString.split("&");
        Map<String, String> map = new HashMap<>();
        for (String s : splitQuery) {
            String[] kv = s.split("=");
            map.put(kv[0], kv[1]);
        }
        return map;
    }

    private String getResponseBody(String method, String line) {
        String uri = getUri(line);
        if (uri.equals("/") && method.equals("GET")) {
            return "Hello world!";
        }
        if ((uri.equals("/login") || uri.equals("/login.html")) && method.equals("GET")) {
            printUserLog(line);
            return modelToView("/login.html");
        }
        return modelToView(uri);
    }

    private void printUserLog(String line) {
        Map<String, String> parameters = getParameters(line);
        log.info("id: %d, account: '%s', email: '%s', password: '%s'".formatted(
                parameters.get("id"), parameters.get("account"), parameters.get("email"), parameters.get("password")
        ));
    }

    private String getExtension(String uri) {
        if (uri.endsWith(".html")) {
            return "html";
        }
        if (uri.endsWith(".css")) {
            return "css";
        }
        return "html";
    }

    @Nonnull
    private String modelToView(String uri) {
        final URL resource = getClass().getClassLoader().getResource(RESOURCES_PREFIX + uri);
        if (resource == null) {
            throw new IllegalArgumentException("존재하지 않는 파일 명입니다. 파일 경로를 확인해주세요.");
        }
        try {
            return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
