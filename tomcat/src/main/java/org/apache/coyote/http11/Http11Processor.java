package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final List<HttpRequestHandler> httpRequestHandlers = List.of(
            new HomeHttpRequestHandler(),
            new HtmlRequestHandler(),
            new CssRequestHandler(),
            new JsRequestHandler(),
            new LoginRequestHandler(),
            new RegisterGetRequestHandler()
    );

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
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream();
                final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            String rawHttpRequest = readRawHttpRequest(bufferedReader);
            handle(HttpRequest.from(rawHttpRequest), outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readRawHttpRequest(final BufferedReader bufferedReader) throws IOException {
        List<String> headerLines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            headerLines.add(line);
        }
        int contentLength = headerLines.stream()
                .filter(l -> l.toLowerCase().startsWith("content-length"))
                .map(l -> Integer.parseInt(l.split(":")[1].trim()))
                .findFirst()
                .orElse(0);

        char[] body = new char[contentLength];
        if (contentLength > 0) {
            bufferedReader.read(body, 0, contentLength);
        }
        
        return String.join("\r\n", headerLines)
                + "\r\n\r\n"
                + new String(body);
    }

    private void handle(final HttpRequest httpRequest, final OutputStream outputStream) throws IOException {
        for (HttpRequestHandler handler : httpRequestHandlers) {
            if (handler.support(httpRequest)) {
                String response = handler.response(httpRequest);
                outputStream.write(response.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                return;
            }
        }
    }
}
