package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.coyote.Processor;
import org.apache.coyote.ServletContainer;
import org.apache.coyote.http11.handler.DefaultResourceHandler;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final ServletContainer servletContainer;
    private final RequestHandler requestHandler;
    private final Socket connection;

    public Http11Processor(ServletContainer servletContainer, Socket connection) {
        this.servletContainer = servletContainer;
        this.connection = connection;
        this.requestHandler = new DefaultResourceHandler();
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

            HttpRequest httpRequest = HttpRequest.readFrom(requestBufferedReader);
            log.info("request : {}", httpRequest);
            HttpResponse httpResponse = new HttpResponse(httpRequest);

            service(httpRequest, httpResponse);

            outputStream.write(httpResponse.toHttpMessage().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            servletContainer.service(httpRequest, httpResponse);
        } catch (Exception e) {
            //todo 예외 처리
        }
    }

    private void getResponse(HttpRequest httpRequest) throws IOException {
        HttpResponse httpResponse = new HttpResponse(httpRequest);
        requestHandler.handle(httpRequest, httpResponse);
    }
}
