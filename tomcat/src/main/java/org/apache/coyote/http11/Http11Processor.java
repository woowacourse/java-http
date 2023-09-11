package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.common.HttpHeader;
import nextstep.jwp.common.HttpRequest;
import nextstep.jwp.common.HttpResponse;
import org.apache.controller.Controller;
import org.apache.controller.ControllerAdapter;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String httpLine = readHttpLine(reader);
            List<String> headers = readHeaders(reader);

            HttpRequest httpRequest = HttpRequest.of(httpLine, headers, readBody(HttpHeader.from(headers), reader));
            HttpResponse httpResponse = new HttpResponse();

            Controller controller = ControllerAdapter.findController(httpRequest.getHttpLine().getUrl());
            controller.service(httpRequest, httpResponse);

            String serialize = httpResponse.serialize();
            outputStream.write(serialize.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | IllegalArgumentException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readHttpLine(BufferedReader reader) throws IOException {
        return reader.readLine();
    }

    private List<String> readHeaders(BufferedReader reader) throws IOException {
        List<String> headers = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            if ("".equals(line)) {
                break;
            }
            headers.add(line);
        }
        return headers;
    }

    private String readBody(HttpHeader header, BufferedReader reader) throws IOException {
        if (header.hasHeader("Content-Length")) {
            int contentLength = Integer.parseInt(header.findValue("Content-Length"));
            char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            return new String(buffer);
        }
        return "";
    }
}
