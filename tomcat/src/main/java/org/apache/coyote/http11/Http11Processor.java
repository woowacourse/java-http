package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.catalina.controller.ControllerMapper;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.Processor;
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
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {

            Request request = new Request(reader);
            Response response = new Response();
            execute(request, response);

            outputStream.write(response.buildHttpMessage().getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void execute(Request request, Response response) throws Exception {
        log.info("request path = {}", request.getPath());
        if (isResourceFile(request.getPath())) {
            response.addFileBody(request.getPath());
            return;
        }
        Controller controller = ControllerMapper.find(request.getPath());
        controller.service(request, response);
    }

    private boolean isResourceFile(String requestPath) {
        return requestPath.contains(".");
    }
}
