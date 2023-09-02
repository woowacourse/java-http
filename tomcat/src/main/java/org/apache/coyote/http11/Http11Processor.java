package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String httpRequest = br.readLine();
            String[] requestInfos = httpRequest.split(" ");

            String requestFile = requestInfos[1];

            StringBuilder responseBody = new StringBuilder();
            if (requestFile.equals("/")) {
                responseBody.append("Hello world!");
            } else {
                ClassLoader classLoader = getClass().getClassLoader();
                URL resource = classLoader.getResource("static" + requestFile);

                File file = new File(resource.getFile());

                Path path = Paths.get(file.getAbsolutePath());

                InputStream fileInputStream = Files.newInputStream(path);
                br = new BufferedReader(new InputStreamReader(fileInputStream));


                String line;
                while ((line = br.readLine()) != null) {
                    responseBody.append(line).append('\n');
                }
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.toString().getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
