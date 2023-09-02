package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            List<String> headers = getHeader(reader);
            HttpRequest httpRequest = HttpRequest.from(headers);

            for (String header : headers) {
                System.out.println(header);
            }
            System.out.println("-----------------");

            if (httpRequest.getUri().contains("/index.html")) {
                URL fileUrl = this.getClass()
                        .getClassLoader()
                        .getResource("static" + "/index.html");

                Path path = new File(Objects.requireNonNull(fileUrl).getFile()).toPath();
                String responseBody = new String(Files.readAllBytes(path));

                String response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());

            } else if (httpRequest.getUri().contains("css")) {
                URL fileUrl = this.getClass()
                        .getClassLoader()
                        .getResource("static/css" + "/styles.css");

                if (Objects.isNull(fileUrl)) {
                    return;
                }

                Path path = new File(Objects.requireNonNull(fileUrl).getFile()).toPath();
                String responseBody = new String(Files.readAllBytes(path));

                String response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/css ",
                        "Content-Length: " + responseBody.length() + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
            } else if (httpRequest.getUri().contains("js")) {
                URL fileUrl;
                if (headers.get(0).contains("scripts")) {
                    fileUrl = this.getClass()
                            .getClassLoader()
                            .getResource("static/js" + "/scripts.js");
                } else {
                    String fileDir = headers.get(0).split(" ")[1];
                    String[] fileDirSplits = fileDir.split("assets/");
                    fileUrl = this.getClass()
                            .getClassLoader()
                            .getResource("static/assets/" + fileDirSplits[1]);
                }

                Path path = new File(Objects.requireNonNull(fileUrl).getFile()).toPath();
                String responseBody = new String(Files.readAllBytes(path));

                String response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());

            }

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> getHeader(final BufferedReader reader) throws IOException {
        List<String> lines = new ArrayList<>();

        String line = Objects.requireNonNull(reader.readLine());
        while (!line.isEmpty()) {
            lines.add(line);
            line = reader.readLine();
        }

        return lines;
    }
}
