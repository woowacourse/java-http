package org.apache.coyote.http11;

import com.techcourse.controller.Controller;
import com.techcourse.controller.RequestMapping;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final SessionManager SESSION_MANAGER = new SessionManager();

    private final RequestMapping requestMapping = new RequestMapping(SESSION_MANAGER);
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

            HttpRequest httpRequest = HttpRequestParser.parse(bufferedReader);

            HttpResponse httpResponse = new HttpResponse();

            handle(httpRequest, httpResponse);

            write(outputStream, httpResponse);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    private void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        Controller controller = requestMapping.getController(httpRequest);
        controller.service(httpRequest, httpResponse);
    }

    private void write(final OutputStream outputStream, HttpResponse httpResponse) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(httpResponse.getVersion() + " " + httpResponse.getStatus() + " ");
        for (Map.Entry<String, String> header : httpResponse.getHeaders().entrySet()) {
            lines.add(header.getKey() + ": " + header.getValue() + " ");
        }
        lines.add("");
        lines.add(httpResponse.getBody());
        final var response = String.join("\r\n", lines);
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
