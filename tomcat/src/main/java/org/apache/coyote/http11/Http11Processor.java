package org.apache.coyote.http11;

import org.apache.coyote.config.AppConfig;
import org.apache.coyote.dto.RequestInfo;
import org.apache.coyote.router.RequestRouter;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.util.PostBodyParser;
import org.apache.coyote.util.RequestLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestRouter requestRouter;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestRouter = AppConfig.getInstance().getRequestRouter();
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
             final var reader = new BufferedReader(new InputStreamReader(inputStream))) {

            final String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            final String response = createResponse(requestLine, reader);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createResponse(final String requestLine, final BufferedReader reader) throws IOException {
        RequestInfo requestInfo = RequestLineParser.parse(requestLine);
        log.info(requestInfo.method());
        if (requestInfo.method().equals("POST")) {
            Map<String, String> postParams = PostBodyParser.parse(reader);
            requestInfo = new RequestInfo(requestInfo.method(), requestInfo.path(), postParams);
        }
        return requestRouter.handleRoute(requestInfo.method(), requestInfo.path(), requestInfo.queryParams());
    }

}