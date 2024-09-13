package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.catalina.handler.StaticResourceHandler;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseWriter;
import org.apache.coyote.http11.response.ResponseHeader;
import org.apache.coyote.http11.response.StatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.was.controller.FrontController;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final StaticResourceHandler staticResourceHandler;

    public Http11Processor(Socket connection) {
        this.connection = connection;
        this.staticResourceHandler = StaticResourceHandler.getInstance();
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

            HttpRequest request = readRequest(inputStream);
            HttpResponse response = new HttpResponse();

            processRequest(request, response);
            writeResponse(outputStream, response);

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static HttpRequest readRequest(InputStream inputStream) throws IOException {
        HttpRequestParser requestParser = HttpRequestParser.getInstance();
        HttpRequest request = requestParser.parse(inputStream);

        RequestLine requestLine = request.getRequestLine();
        log.info("[Request] request Line: {}, {}, {}",
                requestLine.getVersion(), requestLine.getMethod(), requestLine.getPath());

        Map<String, String> header = request.getHeader();
        String formattedHeader = header.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
        log.info("[Request] request header: {}", formattedHeader);

        return request;
    }

    private static void writeResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        StatusLine statusLine = response.getStatusLine();
        log.info("[Response] status Line: {}, {}, {}",
                statusLine.getVersion(), statusLine.getStatusCode(), statusLine.getStatusMessage());

        ResponseHeader header = response.getHeader();
        String formattedHeader = header.getHeaders().entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
        log.info("[Response] response header: {}", formattedHeader);

        HttpResponseWriter responseWriter = HttpResponseWriter.getInstance();
        outputStream.write(responseWriter.write(response));
        outputStream.flush();
    }

    private void processRequest(HttpRequest request, HttpResponse response) throws IOException {
        if (staticResourceHandler.canHandleRequest(request)) {
            staticResourceHandler.handle(request, response);

        } else {
            FrontController frontController = FrontController.getInstance();
            frontController.service(request, response);
        }
    }
}
