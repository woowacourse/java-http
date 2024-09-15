package org.apache.coyote.http11;

import com.techcourse.controller.Controller;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(Socket connection) {
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
            String response = getResponse(inputStream);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String getResponse(InputStream inputStream) throws Exception {
        HttpRequestParser httpRequestParser = HttpRequestParser.getInstance();
        HttpResponseParser httpResponseParser = HttpResponseParser.getInstance();
        RequestMapper requestMapper = RequestMapper.getInstance();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        HttpRequest httpRequest = httpRequestParser.parseRequest(bufferedReader);
        HttpResponse httpResponse = new HttpResponse();
        Controller controller = requestMapper.mapRequest(httpRequest);
        controller.service(httpRequest, httpResponse);
        return httpResponseParser.parseResponse(httpResponse);
    }
}
