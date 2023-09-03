package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.handler.Handler;
import org.apache.coyote.handlermapping.HandlerMapping;
import org.apache.coyote.handlermapping.HandlerMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String startLine = bufferedReader.readLine();

            HttpRequest request = new HttpRequest(startLine);
            HandlerMatcher handlerMatcher = new HandlerMatcher(HttpMethod.valueOf(request.method()), request.uri());
            HandlerMapping.init();
            if (!HandlerMapping.canHandle(handlerMatcher)) {
                Path path = Paths.get(getClass().getClassLoader().getResource("static" + "/" + "404.html").getPath());
                List<String> contents = Files.readAllLines(path);
                String responseBody = String.join("\n", contents) + "\n";
                String response = String.join("\r\n",
                        "HTTP/1.1 404 Not Found ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }
            Handler handler = HandlerMapping.getHandler(handlerMatcher);
            HttpResponse response = handler.handle(request);
            outputStream.write(response.getResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
