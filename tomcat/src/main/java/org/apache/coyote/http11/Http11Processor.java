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

            if (!requestLine.isBlank()) {
                String method = requestLine.split(" ")[0];
                String url = requestLine.split(" ")[1];
                String version = requestLine.split(" ")[2];

                String responseBody = "Hello world!";

                if (method.equals("GET") && url.equals("/index.html")) {
                    URL resource = getClass().getClassLoader().getResource("static/index.html");

                    if (resource != null) {
                        responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
                        responseBody = responseBody.replace("\r\n", "\n");
                    }
                }

                final String response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
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
