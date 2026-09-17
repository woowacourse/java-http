package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

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
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String[] requestLine = bufferedReader.readLine().split(" ");
            Map<String, String> header = new HashMap<>();
            while (true) {
                String line = bufferedReader.readLine();
                if ("".equals(line)) {
                    break;
                }
                String[] headerLine = line.split(":");
                header.put(headerLine[0], headerLine[1].trim());
            }
            String contentLength = header.get("Content-Length");
            if(contentLength != null) {
                char[] buffer = new char[Integer.parseInt(contentLength)];
                int count = bufferedReader.read(buffer, 0, Integer.parseInt(contentLength));
                String requestBody = new String(buffer, 0, count);
            }
            if ("/".equals(requestLine[1])) {
                final var responseBody = "Hello world!";

                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
                outputStream.flush();
            } else {
                if (requestLine[1].equals("/index.html")) {
                    URL resource = getClass().getClassLoader().getResource("static" + requestLine[1]);
                    Path path = Path.of(resource.toURI());
                    String responseLine = "HTTP/1.1 200 OK ";
                    String contentType = "Content-Type: text/html;charset=utf-8 ";
                    String responseBody = Files.readString(path);
                    String length = "Content-Length: " + responseBody.getBytes().length + " ";

                    final var response = String.join("\r\n",
                            responseLine,
                            contentType,
                            length,
                            "",
                            responseBody);
                    outputStream.write(response.getBytes());
                    outputStream.flush();
                }
            }
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }
}
