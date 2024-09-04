package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()
        ) {
            String line = readHeaders(inputStream);
            String uri = line.split(" ")[1];
            String queryString = "";
            int queryIndex = uri.indexOf("?");
            if (queryIndex != -1) {
                queryString = uri.substring(queryIndex + 1);
                uri = uri.substring(0, queryIndex);
            }
            String fileExtension = "";

            int index = uri.lastIndexOf(".");
            String fileName = uri;
            if (index == -1) {
                fileExtension = "html";
            } else {
                fileName = uri.substring(0, index);
                fileExtension = uri.substring(index + 1);
            }

            // uri는 /index.html도 가능하고, /login도 가능하다
            log.debug("uri: {}, fileExtension: {}, queryString: {}", uri, fileExtension, queryString);

            Map<String, String> params = parseQueryString(queryString);

            Path filePath = Paths.get(getClass().getClassLoader().getResource("static" + fileName + "." + fileExtension).getPath());
            List<String> requestHeaders = Files.readAllLines(filePath);

            final String responseBody = String.join("\r\n", requestHeaders);
            String contentType = createContentType(fileExtension);
            String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String extractFileExtension(String requestUri) {
        int index = requestUri.lastIndexOf(".");
        if (index == -1) {
            return "html";
        }
        return requestUri.substring(index + 1);
    }

    private String readHeaders(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        return bufferedReader.readLine();
    }

    private String createContentType(String fileExtension) {
        String contentType;
        if (fileExtension.endsWith("html")) {
            contentType = "text/html;charset=utf-8";
        } else if (fileExtension.endsWith("css")) {
            contentType = "text/css;charset=utf-8";
        } else {
            contentType = "text/plain;charset=utf-8";
        }
        return contentType;
    }

    private Map<String, String> parseQueryString(String queryString) {
        if (queryString.isEmpty()) {
            return new HashMap<>();
        }
        return Arrays.stream(queryString.split("&"))
                .map(s -> s.split("="))
                .collect(Collectors.toMap(s -> s[0], s -> s[1]));
    }
}
