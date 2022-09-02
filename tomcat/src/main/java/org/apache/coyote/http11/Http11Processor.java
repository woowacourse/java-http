package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import nextstep.jwp.exception.UncheckedServletException;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String[] startLine = bufferedReader.readLine().split(" ");

            String httpMethod = startLine[0];
            String httpUrl = startLine[1];
            String fileName = httpUrl.substring(1);

            String responseBody = "Hello world!";
            if (httpMethod.equals("GET") && !httpUrl.equals("/") && fileName.endsWith(".html")) {
                URL resource = getClass().getClassLoader().getResource("static/" + fileName);
                File file = Paths.get(resource.toURI()).toFile();
                FileInputStream fis = new FileInputStream(file);

                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                final StringBuilder stringBuilder = new StringBuilder();
                while (true) {
                    String s = br.readLine();
                    if (s == null) {
                        break;
                    }
                    stringBuilder.append(s);
                    stringBuilder.append(System.lineSeparator());
                }
                responseBody = stringBuilder.toString();
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }
}
