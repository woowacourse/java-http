package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {

            String requestLine = bufferedReader.readLine();
            if (requestLine == null || requestLine.isBlank()) {
                return;
            }

            System.out.println(requestLine);

            List<String> header = new ArrayList<>();
            String headerLine = bufferedReader.readLine();
            while (!headerLine.isBlank()) {
                header.add(headerLine);
                headerLine = bufferedReader.readLine();
            }

            if (!requestLine.isBlank()) {
                String[] requestParts = requestLine.split(" ");
                String method = requestParts[0];
                String uri = requestParts[1];
                String version = requestParts[2];

                String responseBody = "Hello world!";

                if (!"/".equals(uri)) {
                    String resourcePath = "static" + uri;
                    Optional<URL> resource = Optional.ofNullable(getClass().getClassLoader().getResource(resourcePath));

                    if (resource.isPresent()) {
                        responseBody = new String(Files.readAllBytes(new File(resource.get().getFile()).toPath()));
                    }
                }

                final String response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: " + ContentType.findWithCharset(uri) + " ",
                        "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                        "",
                        responseBody);

                bufferedWriter.write(response);
                bufferedWriter.flush();
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
