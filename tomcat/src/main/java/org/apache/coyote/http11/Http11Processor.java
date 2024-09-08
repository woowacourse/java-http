package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.catalina.HandlerAdapter;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.request.RequestBody;
import org.apache.catalina.request.RequestHeader;
import org.apache.catalina.request.StartLine;
import org.apache.catalina.response.HttpResponse;
import org.apache.catalina.response.ResponseParser;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HandlerAdapter handlerAdapter;
    private final ResponseParser responseParser;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.handlerAdapter = new HandlerAdapter();
        this.responseParser = new ResponseParser();
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
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            StartLine startLine = readStartLine(in);
            RequestHeader requestHeader = readHeader(in);
            RequestBody requestBody = readBody(in, requestHeader);
            HttpRequest httpRequest = new HttpRequest(startLine, requestHeader, requestBody);

            HttpResponse httpResponse = handlerAdapter.handle(httpRequest);

            String response = responseParser.parse(httpResponse);
            outputStream.write(response.getBytes());
            outputStream.flush();

            in.close();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private StartLine readStartLine(BufferedReader in) throws IOException {
        return StartLine.parse(in.readLine());
    }

    private RequestHeader readHeader(BufferedReader in) throws IOException {
        String line;
        List<String> headers = new ArrayList<>();
        while ((line = in.readLine()) != null && !line.isBlank()) {
            headers.add(line);
        }
        return RequestHeader.parse(headers);
    }

    private RequestBody readBody(BufferedReader in, RequestHeader requestHeader) throws IOException {
        if (requestHeader.notContainsContentLength()) {
            return RequestBody.empty();
        }
        int contentLength = requestHeader.getContentLength();
        char[] buffer = new char[contentLength];
        in.read(buffer, 0, contentLength);
        String body = URLDecoder.decode(new String(buffer), StandardCharsets.UTF_8);

        return RequestBody.parse(body);
    }
}
