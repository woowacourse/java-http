package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
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
            String line = bufferedReader.readLine();

            var response = "";
            String responseBody = "";
            if (line.startsWith("GET")) {
                String[] request = line.split(" ");
                String url = request[1];

                if (url.equals("/")) {
                    responseBody = "Hello world!";
                    response = String.join("\r\n",
                            "HTTP/1.1 200 OK ",
                            "Content-Type: text/html;charset=utf-8 ",
                            "Content-Length: " + responseBody.getBytes().length + " ",
                            "",
                            responseBody);
                } else if (url.endsWith(".html")) {
                    URL resource = getClass().getClassLoader().getResource("static" + url);
                    if (resource != null) {
                        String file = resource.getFile();
                        if (file != null) {
                            responseBody = new String(Files.readAllBytes(Paths.get(file)));
                            response = String.join("\r\n",
                                    "HTTP/1.1 200 OK ",
                                    "Content-Type: text/html;charset=utf-8 ",
                                    "Content-Length: " + responseBody.getBytes().length + " ",
                                    "",
                                    responseBody);
                        }
                    }
                } else if (url.endsWith(".css")) {
                    URL resource = getClass().getClassLoader().getResource("static" + url);
                    if (resource != null) {
                        String file = resource.getFile();
                        if (file != null) {
                            responseBody = new String(Files.readAllBytes(Paths.get(file)));
                            response = String.join("\r\n",
                                    "HTTP/1.1 200 OK ",
                                    "Content-Type: text/css;charset=utf-8 ",
                                    "Content-Length: " + responseBody.getBytes().length + " ",
                                    "",
                                    responseBody);
                        }
                    }
                } else if (url.endsWith(".js")) {
                    URL resource = getClass().getClassLoader().getResource("static" + url);
                    if (resource != null) {
                        String file = resource.getFile();
                        if (file != null) {
                            responseBody = new String(Files.readAllBytes(Paths.get(file)));
                            response = String.join("\r\n",
                                    "HTTP/1.1 200 OK ",
                                    "Content-Type: text/js;charset=utf-8 ",
                                    "Content-Length: " + responseBody.getBytes().length + " ",
                                    "",
                                    responseBody);
                        }
                    }
                }
            }
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
