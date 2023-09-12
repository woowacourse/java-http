package org.apache.coyote.http11;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.common.FileReader;
import org.apache.coyote.http11.controller.AuthController;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.DefaultController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import static org.apache.coyote.http11.common.HttpHeaderType.CONTENT_LENGTH;
import static org.apache.coyote.http11.common.HttpHeaderType.CONTENT_TYPE;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpRequestReader requestReader = new HttpRequestReader();
    private final RequestMapping requestMapping = new RequestMapping(
            new AuthController(),
            new RegisterController(),
            new DefaultController()
    );

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
        try (var inputStream = connection.getInputStream();
             var outputStream = connection.getOutputStream()) {

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            HttpRequest httpRequest = requestReader.readHttpRequest(bufferedReader);
            HttpResponse httpResponse = new HttpResponse(httpRequest.httpVersion());

            Controller controller = requestMapping.getController(httpRequest);
            controller.service(httpRequest, httpResponse);

            httpResponse.setBody(FileReader.readFile(httpResponse.getResponseFileName()));
            httpResponse.addHeader(CONTENT_LENGTH.getValue(), String.valueOf(httpResponse.getBody().getBytes().length));
            httpResponse.addHeader(CONTENT_TYPE.getValue(), httpRequest.getContentTypeByAcceptHeader());

            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            bufferedOutputStream.write(httpResponse.format().getBytes());
            bufferedOutputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
