package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.BaseHandler;
import org.apache.coyote.http11.handler.LoginHandler;
import org.apache.coyote.http11.handler.RegisterHandler;
import org.apache.coyote.http11.handler.StaticFileHandler;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
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
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {

            RequestLine requestLine = readRequestLine(reader);
            RequestHeader requestHeader = readHeader(reader);
            RequestBody requestBody = readRequestBody(reader, requestHeader);
            HttpResponse response = handleHttpRequest(requestLine, requestHeader, requestBody);
            writer.write(response.toString());
            writer.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private RequestLine readRequestLine(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        return RequestLine.from(line);
    }

    private RequestHeader readHeader(BufferedReader reader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.equals("")) {
                break;
            }
            lines.add(line);
        }
        return RequestHeader.from(lines);
    }

    private RequestBody readRequestBody(final BufferedReader reader, final RequestHeader requestHeader) throws IOException {
        if (requestHeader.getHeaderValue("Content-Type") == null) {
            return null;
        }
        int contentLength = Integer.parseInt(requestHeader.getHeaderValue("Content-Length"));
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        return RequestBody.from(requestBody);
    }

    private HttpResponse handleHttpRequest(final RequestLine requestLine, final RequestHeader requestHeader, final RequestBody requestBody) {
        String requestURI = requestLine.getRequestURI();
        if (requestURI.equals("/")) {
            return BaseHandler.handle();
        }
        if (requestURI.startsWith("/login")) {
            return LoginHandler.handle(requestLine, requestHeader, requestBody);
        }
        if (requestURI.startsWith("/register")) {
            return RegisterHandler.handle(requestLine, requestHeader, requestBody);
        }
        return StaticFileHandler.handle(requestLine.getRequestURI(), requestHeader);
    }
}
