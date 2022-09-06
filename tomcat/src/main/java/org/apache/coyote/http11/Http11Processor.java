package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.ResourceNotFoundException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.generator.NotFoundResponseGenerator;
import org.apache.coyote.http11.response.generator.ResponseGenerator;
import org.apache.coyote.http11.response.generator.ResponseGeneratorFinder;
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
        try (var inputStream = connection.getInputStream();
             var outputStream = connection.getOutputStream();
             var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            String response = getResponse(httpRequest);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }


    private String getResponse(HttpRequest httpRequest) throws IOException {
        ResponseGenerator responseGenerator = ResponseGeneratorFinder.find(httpRequest);
        try {
            HttpResponse httpResponse = responseGenerator.generate(httpRequest);
            return httpResponse.getResponse();
        } catch (ResourceNotFoundException e) {
            responseGenerator = NotFoundResponseGenerator.getInstance();
            HttpResponse httpResponse = responseGenerator.generate(httpRequest);
            return httpResponse.getResponse();
        }
    }
}
