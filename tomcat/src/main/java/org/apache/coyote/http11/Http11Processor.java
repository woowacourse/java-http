package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import nextstep.jwp.presentation.Controller;
import nextstep.jwp.presentation.RequestMapping;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            HttpRequest request = HttpRequest.from(bufferedReader);
            Controller controller = RequestMapping.getController(request);
            log.info("controller : {}", controller);

            HttpResponse response = new HttpResponse();
            controller.service(request, response);
            submitResponse(outputStream, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void submitResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        String httpResponse = response.to();
        outputStream.write(httpResponse.getBytes());
        outputStream.flush();
    }
}
