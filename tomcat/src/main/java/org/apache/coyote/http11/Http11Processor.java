package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.HandlerMapper;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ResourceProvider resourceProvider;
    private final HandlerMapper handlerMapper;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.resourceProvider = new ResourceProvider();
        this.handlerMapper = new HandlerMapper();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            final var outputStream = connection.getOutputStream()) {
            HttpRequest httpRequest = HttpRequest.makeRequest(inputReader);
            String response = getResponse(httpRequest);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(HttpRequest httpRequest) {
        if (resourceProvider.haveResource(httpRequest.getRequestLine().getPath())) {
            return staticResourceResponse(httpRequest.getRequestLine().getPath());
        }
        if (handlerMapper.haveAvailableHandler(httpRequest)) {
            return controllerResponse(httpRequest);
        }
        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 12 ",
            "",
            "Hello world!");
    }

    private String staticResourceResponse(String resourcePath) {
        String responseBody = resourceProvider.resourceBodyOf(resourcePath);
        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            resourceProvider.contentTypeOf(resourcePath),
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }

    private String controllerResponse(HttpRequest httpRequest) {
        Controller handler = handlerMapper.getHandler(httpRequest);
        HttpResponse<Object> httpResponse = (HttpResponse<Object>) handler.handle(httpRequest);
        return makeResponse(httpResponse);
    }

    private String makeResponse(HttpResponse<Object> httpResponse) {
        StringBuilder response = new StringBuilder();
        response.append(requestLine(httpResponse));
        Optional<String> body = bodyOf(httpResponse);
        if (body.isPresent()) {
            return response.append(responseWithBody(httpResponse, body.get())).toString();
        }
        String str = responseNoBody(httpResponse);
        response.append("\r\n");
        return response.append(str).toString();
    }

    private String requestLine(HttpResponse<Object> httpResponse) {
        HttpStatusCode httpStatusCode = HttpStatusCode.of(httpResponse.getStatusCode());
        return "HTTP/1.1 " + httpStatusCode.getStatusCode() + " " + httpStatusCode.name() + " ";
    }

    private Optional<String> bodyOf(HttpResponse<Object> httpResponse) {
        if (httpResponse.isViewResponse()) {
            return Optional.of(resourceProvider.resourceBodyOf(httpResponse.getViewPath()));
        }
        return Optional.empty();
    }

    private String responseWithBody(HttpResponse<Object> httpResponse, String body) {
        Map<String, String> headers = httpResponse.getHeaders();
        StringJoiner stringJoiner = new StringJoiner("\r\n");
        stringJoiner.add(headerResponse(headers));
        stringJoiner.add(resourceProvider.contentTypeOf(httpResponse.getViewPath()));
        stringJoiner.add("Content-Length: " + body.getBytes().length + " ");
        stringJoiner.add("");
        stringJoiner.add(body);
        return stringJoiner.toString();
    }

    private String headerResponse(Map<String, String> headers) {
        return headers.keySet()
            .stream()
            .map(headerName -> makeHeader(headerName, headers.get(headerName)))
            .collect(Collectors.joining("\r\n"));
    }

    private String makeHeader(String headerName, String value) {
        return headerName + ": " + value;
    }

    private String responseNoBody(HttpResponse<Object> httpResponse) {
        Map<String, String> headers = httpResponse.getHeaders();
        StringJoiner stringJoiner = new StringJoiner("\r\n");
        stringJoiner.add(headerResponse(headers));
        stringJoiner.add("");
        return stringJoiner.toString();
    }
}
