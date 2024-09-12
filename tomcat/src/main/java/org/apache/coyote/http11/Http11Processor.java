package org.apache.coyote.http11;

import com.techcourse.controller.Controller;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ResourceFileLoader;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping;
    private final RequestProcessor requestProcessor;
    private final OutputProcessor outputProcessor;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestMapping = new RequestMapping();
        this.requestProcessor = new RequestProcessor();
        this.outputProcessor = new OutputProcessor();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = HttpRequest.createEmptyHttpRequest();
            requestProcessor.process(inputStream, httpRequest);
            HttpResponse httpResponse = HttpResponse.createEmptyResponse();

            RequestPathType requestPathType = httpRequest.getRequestPathType();
            if (requestPathType.isStaticResource()) {
                processStaticResourceResponse(httpRequest, httpResponse);
            }
            if (requestPathType.isAPI()) {
                Controller controller = requestMapping.getController(httpRequest);
                controller.service(httpRequest, httpResponse);
            }

            outputProcessor.process(outputStream, httpResponse);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void processStaticResourceResponse(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String responseBody = ResourceFileLoader.loadStaticFileToString(httpRequest.getRequestPath());
        httpResponse.setHttpStatus(HttpStatus.OK);
        httpResponse.setContentType(ContentType.toContentType(httpRequest.getRequestPath().split("\\.")[1]));
        httpResponse.setResponseBody(responseBody);
    }
}
