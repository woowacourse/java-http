package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import org.apache.coyote.Processor;
import com.techcourse.controller.Controller;
import com.techcourse.controller.RequestMapping;
import org.apache.coyote.http11.exception.NotFoundException;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httprequest.HttpRequestConvertor;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String NOT_FOUND_PATH = "/404.html";
    private static final String CHECKED_STATIC_RESOURCE = ".";

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
        try (
                final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()
        ) {
            var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest httpRequest = HttpRequestConvertor.convertHttpRequest(bufferedReader);

            HttpResponse httpResponse = getHttpResponse(httpRequest);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse getHttpResponse(HttpRequest httpRequest) throws IOException, URISyntaxException {
        try {
            if (isStaticResource(httpRequest)) {
                return HttpResponse.ok(httpRequest)
                        .staticResource(httpRequest.getPath())
                        .build();
            }
            RequestMapping requestMapping = new RequestMapping();
            Controller controller = requestMapping.getController(httpRequest);

            return controller.service(httpRequest);
        } catch (NotFoundException e) {
            return HttpResponse.found(httpRequest)
                    .location(NOT_FOUND_PATH)
                    .build();
        }
    }

    private boolean isStaticResource(HttpRequest httpRequest) {
        return httpRequest.getMethod() == HttpMethod.GET && httpRequest.getPath().contains(CHECKED_STATIC_RESOURCE);
    }
}
