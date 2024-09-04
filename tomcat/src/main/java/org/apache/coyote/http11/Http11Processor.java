package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();

            if (line == null || line.isEmpty()) {
                return;
            }

            HttpRequest httpRequest = new HttpRequest(line);

            String response = response(httpRequest.getPath());

            System.out.println(response);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String response(String path) throws URISyntaxException, IOException {
        String responseBody;
        String contentType = "text/html;charset=utf-8 ";

        if (path.equals("/")) {
            responseBody = "Hello world!";
        } else {
            path = "static" + path;
            URL resource = getClass().getClassLoader().getResource(path);
            if (resource.getPath().endsWith(".css")) {
                contentType = "text/css;charset=utf-8 ";
            }
            responseBody = new String(Files.readAllBytes(Path.of(resource.toURI())));
        }

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType,
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
