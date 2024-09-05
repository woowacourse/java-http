package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.HandlerMapper;
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
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            RequestLine requestLine = new RequestLine(bufferedReader.readLine());

            String body = makeResponseBody(requestLine);

            String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + findResponseContentType(requestLine.getRequestURI()) + ";charset=utf-8 ",
                    "Content-Length: " + body.getBytes().length + " ",
                    "",
                    body);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String makeResponseBody(RequestLine requestLine) throws IOException {
        if (HandlerMapper.hasHandler(requestLine.getRequestURI())) {
            Controller controller = HandlerMapper.mapTo(requestLine.getRequestURI());
            String viewUri = controller.handle(requestLine);
            Path path = new ViewResolver().findViewPath(viewUri);
            return Files.readString(path);
        }
        Path path = new ViewResolver().findViewPath(requestLine.getRequestURI());
        return Files.readString(path);
    }

    private String findResponseContentType(String url) {
        String[] extension = url.split("\\.");
        if (extension.length < 2) {
            return "text/html";
        }
        return "text/" + extension[1];
    }
}
