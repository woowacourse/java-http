package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final List<HttpRequestHandler> httpRequestHandlers = List.of(
            new HomeHttpRequestHandler(),
            new IndexHtmlRequestHandler(),
            new CssRequestHandler(),
            new LoginRequestHandler()
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            RequestStartLine requestStartLine = createRequestStartLine(bufferedReader.readLine());

            handle(requestStartLine, outputStream);
            bufferedReader.close();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handle(final RequestStartLine requestStartLine, final OutputStream outputStream) throws IOException {
        for (HttpRequestHandler handler : httpRequestHandlers) {
            if (handler.support(requestStartLine)) {
                String response = handler.response(requestStartLine);
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }
        }
    }

    private RequestStartLine createRequestStartLine(final String startLine) {
        String[] startLines = startLine.split(" ");
        String requestMethod = startLines[0];
        String requestUrl = startLines[1];
        String requestHttpVersion = startLines[2];

        return new RequestStartLine(
                RequestMethod.valueOf(requestMethod),
                requestUrl,
                requestHttpVersion
        );
    }
}
