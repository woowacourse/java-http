package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.exception.HttpRequestException;
import org.apache.coyote.http11.request.mapping.RequestMapping;
import org.apache.coyote.http11.response.HttpResponse;
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

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String startLine = br.readLine();
            if (startLine == null) {
                return;
            }

            HttpRequest request = HttpRequest.from(br, startLine);

            Controller controller = RequestMapping.getController(request);
            HttpResponse response = controller.service(request);

            outputStream.write(response.convertToBytes());
            outputStream.flush();
        } catch (HttpRequestException httpRequestException) {
            log.warn(httpRequestException.getMessage(), httpRequestException);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
