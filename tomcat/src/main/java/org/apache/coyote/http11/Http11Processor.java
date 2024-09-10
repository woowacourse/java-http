package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.NoHandlerException;
import org.apache.coyote.http11.handler.DefaultResourceHandler;
import org.apache.coyote.http11.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final List<RequestHandler> requestHandlers;
    private final Socket connection;

    public Http11Processor(Socket connection) {
        this.connection = connection;
        this.requestHandlers = List.of(
                //new MethodHandler(),
                new DefaultResourceHandler()
        );
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

            Request request = Request.parseFrom(getPlainRequest(requestBufferedReader));
            log.info("request : {}", request);
            String response = getResponse(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(Request request) throws IOException {
        for (RequestHandler requestHandler : requestHandlers) {
            String response = requestHandler.handle(request);
            if (response != null) {
                return response;
            }
        }
        throw new NoHandlerException("핸들러가 존재하지 않습니다. request : " + request);
    }

    private List<String> getPlainRequest(BufferedReader requestBufferedReader) throws IOException {
        return requestBufferedReader.lines().toList();
    }
}
