package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
             OutputStream outputStream = connection.getOutputStream()) {

            HttpResponse response = executeRequest(bufferedReader);

            outputStream.write(response.makeResponse().getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse executeRequest(BufferedReader bufferedReader) throws Exception {
        HttpRequest httpRequest = HttpRequest.from(bufferedReader);
        HttpResponse response = new HttpResponse();
        Controller controller = RequestMapping.getController(httpRequest);
        controller.service(httpRequest, response);
        return response;
    }
}
