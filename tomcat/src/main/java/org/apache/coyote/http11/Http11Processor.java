package org.apache.coyote.http11;

import common.http.ControllerManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.StaticControllerManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    private ControllerManager controllerManager;

    public Http11Processor(Socket connection, ControllerManager controllerManager) {
        this.connection = connection;
        this.controllerManager = controllerManager;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            HttpRequest httpRequest = HttpRequestParser.parse(reader);
            HttpResponse httpResponse = new HttpResponse();

            controllerManager.service(httpRequest, httpResponse);

            serviceIfHasStaticResourcePath(httpRequest, httpResponse);

            outputStream.write(httpResponse.toString().getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void serviceIfHasStaticResourcePath(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.hasStaticResourcePath() || httpResponse.hasStaticResourcePath()) {
            controllerManager = new StaticControllerManager();
            controllerManager.service(httpRequest, httpResponse);
        }
    }
}
