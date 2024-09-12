package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import org.apache.catalina.Mapper;
import org.apache.catalina.controller.DefaultController;
import org.apache.catalina.controller.NotFoundController;
import org.apache.coyote.Controller;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestReader;
import org.apache.coyote.http11.request.RequestReader;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Mapper mapper;

    public Http11Processor(Socket connection, Mapper mapper) {
        this.connection = connection;
        this.mapper = mapper;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (BufferedReader bufferedReader = createReader(connection);
             BufferedWriter bufferedWriter = createWriter(connection)) {

            HttpRequest httpRequest = handleRequest(bufferedReader);
            HttpResponse httpResponse = handleResponse(httpRequest);

            bufferedWriter.write(httpResponse.toMessage());
            bufferedWriter.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private BufferedReader createReader(Socket connection) throws IOException {
        return new BufferedReader(new InputStreamReader(connection.getInputStream()));
    }

    private BufferedWriter createWriter(Socket connection) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
    }

    private HttpRequest handleRequest(BufferedReader bufferedReader) throws IOException {
        RequestReader requestReader = new HttpRequestReader(bufferedReader);
        return HttpRequest.read(requestReader);
    }

    private HttpResponse handleResponse(HttpRequest httpRequest) throws Exception {
        HttpResponse httpResponse = new HttpResponse();

        mapper.getController(httpRequest)
                .orElseGet(() -> handleStaticRequest(httpRequest))
                .service(httpRequest, httpResponse);

        return httpResponse;
    }

    private Controller handleStaticRequest(HttpRequest httpRequest) {
        return httpRequest.isStaticResource() ? new DefaultController() : new NotFoundController();
    }
}
