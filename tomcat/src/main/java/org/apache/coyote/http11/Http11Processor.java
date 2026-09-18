package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            // URL 파싱
            String line = bufferedReader.readLine();
            final String[] tokens = line.split(" ", 3);
            final String uri = tokens[1];
            String path = tokens[1];

            if (uri.contains("?")) {
                int index = uri.indexOf("?");
                path = uri.substring(0, index);
                String queryString = uri.substring(index + 1);

                Map<String, String> paramsMap = getParamsMap(queryString);
            }

            final var responseBody = createResponseBody(path);
            final String contentType = getContentType(path);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.length + " ",
                    "",
                    new String(responseBody));

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | URISyntaxException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private byte[] createResponseBody(String requestTarget) throws IOException, URISyntaxException {
        final String resourcePath = "static" + requestTarget;
        if (requestTarget.equals("/")) {
            return "Hello world!".getBytes();
        }

        final URL resource = Objects.requireNonNull(getClass().getClassLoader().getResource(resourcePath));
        final Path path = new File(resource.getFile()).toPath();
        return Files.readAllBytes(path);
    }

    private String getContentType(String path) {
        if (path.equals("/") || path.endsWith(".html")){
            return "text/html;charset=utf-8";
        }
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        return "application/octet-stream";
    }

    @Nonnull
    private Map<String, String> getParamsMap(String queryString) {
        String[] data = queryString.split("\\&");
        Map<String, String> paramsMap = new HashMap<>();
        for (String d : data) {
            String[] param = d.split("\\=");
            paramsMap.put(param[0], param[1]);
        }
        return paramsMap;
    }
}
