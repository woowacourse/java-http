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

            RequestMapping requestMapping = RequestMapping.init();
            Controller controller = requestMapping.find(httpRequest.getRequestUri());
            HttpResponse httpResponse = toHttpResponse(httpRequest, controller);

            outputStream.write(httpResponse.getResponse());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse toHttpResponse(final HttpRequest httpRequest, final Controller controller) throws IOException {
        String response = controller.service(httpRequest);
        if (controller.isRest()) {
            return HttpResponse.of(httpRequest, response);
        }
        return HttpResponse.of(httpRequest, ResourceUtil.getResource(response));
    }

    private HttpRequest toHttpRequest(final BufferedReader bufferedReader) throws IOException {
        List<String> requests = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if ("".equals(line)) {
                break;
            }
            System.out.println(line);
            requests.add(line);
        }
        return HttpRequest.from(requests);
    }

    private void addResponseBody(final HttpRequest httpRequest, final HttpResponse httpResponse,
                                 final Controller controller)
            throws IOException {
        if (controller.isRest()) {
            httpResponse.setResponseBody(controller.service(httpRequest));
            return;
        }
        httpResponse.setResponseBody(ResourceUtil.getResource(controller.service(httpRequest)));
    }
}
