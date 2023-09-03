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
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.request.RequestHeaders;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.request.ResponseBody;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.Response;
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
            Request request = readRequest(inputReader);
            String response = getResponse(request);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Request readRequest(BufferedReader inputReader) {
        RequestLine requestLine = new RequestLine(readRequestLine(inputReader));
        RequestHeaders requestHeaders = new RequestHeaders(readHeaders(inputReader));
        ResponseBody responseBody = new ResponseBody(readBody(inputReader, requestHeaders));
        return new Request(requestLine,
            requestHeaders,
            responseBody);
    }

    private String readRequestLine(BufferedReader inputReader) {
        try {
            return inputReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String readHeaders(BufferedReader inputReader) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            String line = inputReader.readLine();
            while (!"".equals(line)) {
                stringBuilder.append(line);
                stringBuilder.append(System.lineSeparator());
                line = inputReader.readLine();
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String readBody(BufferedReader inputReader, RequestHeaders requestHeaders) {
        try {
            String s = requestHeaders.getHeaders().get("Content-Length");
            if (s == null) {
                return null;
            }
            int contentLength = Integer.parseInt(s);
            char[] buffer = new char[contentLength];
            inputReader.read(buffer, 0, contentLength);
            return new String(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getResponse(Request request) {
        if (resourceProvider.haveResource(request.getRequestLine().getPath())) {
            return staticResourceResponse(request.getRequestLine().getPath());
        }
        if (handlerMapper.haveAvailableHandler(request)) {
            return controllerResponse(request);
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

    private String controllerResponse(Request request) {
        Controller handler = handlerMapper.getHandler(request);
        Response response = handler.handle(request);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(requestLine(response));
        Optional<String> body = bodyOf(response);
        if (body.isPresent()) {
            String s = stringBuilder.append(responseWithBody(response, body.get())).toString();
            return s;
        }
        String str = responseNoBody(response);
        stringBuilder.append("\r\n");
        return stringBuilder.append(str).toString();
    }

    private String responseNoBody(Response response) {
        Map headers = response.getHeaders();
        StringJoiner stringJoiner = new StringJoiner("\r\n");
        stringJoiner.add(headerResponse(headers));
        stringJoiner.add("");
        return stringJoiner.toString();
    }

    private String responseWithBody(Response response, String body) {
        Map headers = response.getHeaders();
        StringJoiner stringJoiner = new StringJoiner("\r\n");
        stringJoiner.add(headerResponse(headers));
        stringJoiner.add(resourceProvider.contentTypeOf(response.getViewPath()));
        stringJoiner.add("Content-Length: " + body.getBytes().length + " ");
        stringJoiner.add("");
        stringJoiner.add(body);
        return stringJoiner.toString();
    }

    private String headerResponse(Map headers) {
        return (String) headers.keySet()
            .stream()
            .map(headerName -> makeHeader(headerName, headers.get(headerName)))
            .collect(Collectors.joining("\r\n"));
    }

    private String makeHeader(Object headerName, Object value) {
        String headerKey = (String) headerName;
        String headerValue = (String) value;
        return headerKey + ": " + headerValue;
    }

    private Optional<String> bodyOf(Response response) {
        if (response.isViewResponse()) {
            return Optional.of(resourceProvider.resourceBodyOf(response.getViewPath()));
        }
        return Optional.empty();
    }

    private String requestLine(Response response) {
        HttpResponse httpResponse = HttpResponse.of(response.getStatusCode());
        return "HTTP/1.1 " + httpResponse.getStatusCode() + " " + httpResponse.name() + " ";
    }
}
