package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.DefaultResourceHandler;
import org.apache.coyote.http11.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final RequestHandler requestHandler;
    private final Socket connection;

    public Http11Processor(Socket connection) {
        this.connection = connection;
        this.requestHandler = new DefaultResourceHandler();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (var inputStream = connection.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader requestBufferedReader = new BufferedReader(inputStreamReader);
             var outputStream = connection.getOutputStream()) {

            Request request = Request.readFrom(requestBufferedReader);
            log.info("request : {}", request);
            String response = getResponse(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(Request request) throws IOException {
        return requestHandler.handle(request);
    }
}
