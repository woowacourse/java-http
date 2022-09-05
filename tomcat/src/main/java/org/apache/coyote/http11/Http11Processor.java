package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.HttpStatus;
import org.apache.util.ResourceUtil;
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
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {

            HttpRequest httpRequest = toHttpRequest(bufferedReader);
            HttpResponse httpResponse;
            try {
                Controller controller = RequestMapping.find(httpRequest.getRequestUri());
                httpResponse = toHttpResponse(httpRequest, controller);
            } catch (Exception e) {
                httpResponse = ErrorHandler.handle(e);
            }
            outputStream.write(httpResponse.getResponse());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest toHttpRequest(final BufferedReader bufferedReader) throws IOException {
        List<String> requests = new ArrayList<>();
        String line;
        while (bufferedReader.ready()) {
            line = bufferedReader.readLine();
            if ("".equals(line)) {
                break;
            }
            System.out.println(line);
            requests.add(line);
        }

        StringBuilder requestBody = new StringBuilder();
        while (bufferedReader.ready()) {
            requestBody.append((char) bufferedReader.read());
        }

        return HttpRequest.from(requests, requestBody.toString());
    }

    private HttpResponse toHttpResponse(final HttpRequest httpRequest, final Controller controller) throws IOException {
        String response = controller.service(httpRequest);
        if (controller.isRest()) {
            return HttpResponse.of(HttpStatus.OK, httpRequest, response);
        }
        View view = View.from(response);
        if (view.isRedirect()) {
            return HttpResponse.of(HttpStatus.REDIRECT, httpRequest, ResourceUtil.getResource(view.getValue()));
        }
        return HttpResponse.of(HttpStatus.OK, httpRequest, ResourceUtil.getResource(view.getValue()));
    }

}
