package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            if (line == null) { return; }

            String[] request = line.split(" ");
            String httpMethod = request[0];
            String uri = request[1];
            String protocol = request[2];

            if(httpMethod.equals("GET")){
                final var contentType = parseContentType(uri);
                final var body = getResponseBody(uri);

                final var response = createHttpResponse(body, contentType);
                outputStream.write(response.getBytes());
                outputStream.flush();
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponseBody(String uri) throws IOException {
        URL resource = getResource(uri);
        if(resource == null){
            return "Hello world!";
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private URL getResource(final String uri) {
        if(uri.equals("/")) {
            return null;
        }
        return getClass().getClassLoader().getResource("static" + uri);
    }

    private String parseContentType(String uri) {
        if(uri.endsWith(".css")){
            return "text/css;charset=utf-8";
        } else{
            return "text/html;charset=utf-8";
        }
    }

    private String createHttpResponse(String body, String contentType) throws IOException {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }
}
