package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final String STATIC_ROOT = "static";
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

            HttpRequestHeader header = extractHeader(inputStream);
            URL url = findStaticResource(header.firstLine().requestTarget());
            String contentType = resolveContentType(header);
            final String responseBody = resolveContentOf(url);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
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

    private HttpRequestHeader extractHeader(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        Map<String, String> headers = new HashMap<>();

        String line = br.readLine();
        RequestLine firstLine = RequestLine.from(line);

        do {
            line = br.readLine();
            String[] parts = line.split(": ", 2);
            if (parts.length == 2) {
                headers.put(parts[0], parts[1]);
            }
        } while (!line.isEmpty());
        return new HttpRequestHeader(firstLine, headers);
    }

    private URL findStaticResource(String requestTarget) {
        return getClass().getClassLoader().getResource(STATIC_ROOT + requestTarget);
    }

    private String resolveContentType(HttpRequestHeader header) {
        String accept = header.header().get("Accept");
        if (accept != null && !accept.isEmpty()) {
            String preferred = accept.split(",")[0]
                    .split(";")[0].trim();
            if (!preferred.equals("*/*")) {
                return preferred;
            }
        }

        return "text/html";
    }

    private String resolveContentOf(URL fileUrl) {
        if (fileUrl == null || fileUrl.getPath().endsWith("/")) {
            return "Hello world!";
        }

        try {
            Path path = new File(fileUrl.getFile()).toPath();
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
