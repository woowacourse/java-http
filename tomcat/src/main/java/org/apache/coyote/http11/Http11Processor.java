package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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
             final var outputStream = connection.getOutputStream();
             final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream)))
        {
            final String line = br.readLine(); //GET /index.html HTTP/1.1
            final String[] requestValues = line.split(" "); //[GET, /index.html, HTTP/1.1]

            String requestPath = requestValues[1]; // /index.html
            final URL url = getClass().getClassLoader().getResource("static" + requestPath); // static/index.html

            String responseBody;
            if (requestPath.equals("/")) {
                responseBody = "Hello world!";
            }
            else if (url != null ) {
                Path path = Paths.get(url.toURI());
                responseBody = Files.readString(path);
            }
            else {
                //파일을 못찾은거니까 404 줘야징
                URL notFoundUrl = getClass().getClassLoader().getResource("static/404.html");
                Path path = Paths.get(notFoundUrl.toURI());
                responseBody = Files.readString(path);
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + getContentType(requestPath),
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getContentType(final String requestPath) {
        if (requestPath.endsWith(".css")) {
            return "text/css;charset=utf-8 ";
        }

        return "text/html;charset=utf-8 ";
    }
}
