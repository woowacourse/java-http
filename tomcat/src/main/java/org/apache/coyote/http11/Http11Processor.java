package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.HandlerMapper;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.request.RequestHeaders;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.request.ResponseBody;
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
        return new Request(new RequestLine(readRequestLine(inputReader)),
            new RequestHeaders(readHeaders(inputReader)),
            new ResponseBody(readBody(inputReader)));
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

    private String readBody(BufferedReader inputReader) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            if (!inputReader.ready()) {
                return null;
            }
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

    private String getResponse(Request request) {
        if (resourceProvider.haveResource(request.getRequestLine().getPath())) {
            return staticResourceResponse(request.getRequestLine().getPath());
        }
        if (handlerMapper.haveAvailableHandler(request)) {
            return controllerResponse(request);
        }
        return staticResourceResponse("/404.html");
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
        if (response.getStatusCode() == 302) {
            return redirectResponse(response);
        }
        throw new UnsupportedOperationException();
    }

    private String redirectResponse(Response response) {
        String location = (String) response.getHeaders().get("Location");
        return String.join("\r\n",
            "HTTP/1.1 302 FOUND ",
            "Location: " + location
        );
    }
}
