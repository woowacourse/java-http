package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.catalina.RequestMapping;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final int MAX_WRITE_LENGTH = 10000;

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
             final OutputStream outputStream = connection.getOutputStream();
        ) {
            final String requestMessage = parseRequestMessage(inputStream);
            final HttpRequest httpRequest = new HttpRequest(requestMessage);
            final HttpResponse httpResponse = new HttpResponse();

            final RequestMapping requestMapping = AppConfig.initRequestMapping();
            final Controller controller = requestMapping.findController(httpRequest);
            controller.service(httpRequest, httpResponse);

            final String responseMessage = httpResponse.convertHttpResponseMessage();
            outputStream.write(responseMessage.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseRequestMessage(final InputStream inputStream) throws IOException {
        final byte[] data = new byte[MAX_WRITE_LENGTH];
        final int readDataSize = inputStream.read(data);

        return new String(data, 0, readDataSize);
    }
}
