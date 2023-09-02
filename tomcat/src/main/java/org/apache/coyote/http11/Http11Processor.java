package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String requestLine = bufferedReader.readLine();
            String[] requests = requestLine.split(" ");

            String httpMethod = requests[0];
            String httpUrl = requests[1];
            String fileName = httpUrl.substring(httpUrl.lastIndexOf('/') + 1);
            String response = null;

            if (httpMethod.equals("GET") && httpUrl.equals("/")) {
                response = createResponse("text/plain;", "Hello world!");
            }

            if (httpMethod.equals("GET") && fileName.equals("index.html")) {
                response = createResponse("text/html", readFile("static", fileName));
            }

            if (httpMethod.equals("GET") && fileName.equals("styles.css")) {
                response = createResponse("text/css", readFile("static/css", fileName));
            }

            if (httpMethod.equals("GET") && fileName.endsWith(".js") && !fileName.equals("scripts.js")) {
                response = createResponse("text/javascript", readFile("static/assets", fileName));
            }

            if (httpMethod.equals("GET") && fileName.equals("scripts.js")) {
                response = createResponse("text/javascript", readFile("static/js", fileName));
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createResponse(String contentType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String readFile(String directory, String fileName) throws IOException {
        URL resource = getClass().getClassLoader().getResource(directory + "/" + fileName);
        Path path = Path.of(resource.getPath());
        return Files.readString(path);
    }

}
