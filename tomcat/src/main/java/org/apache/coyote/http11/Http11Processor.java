package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.HandlerAdapter;
import org.apache.coyote.http11.handler.RequestHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.request.HttpRequestStartLine;
import org.apache.coyote.http11.response.HttpResponse;
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
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = createHttpRequest(inputStream);
            RequestHandler requestHandler = findHandler(httpRequest);
            HttpResponse httpResponse = requestHandler.handle(httpRequest);

            outputStream.write(httpResponse.toString().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private RequestHandler findHandler(final HttpRequest httpRequest) {
        HandlerAdapter handlerAdapter = new HandlerAdapter();
        return handlerAdapter.find(httpRequest);
    }

    private HttpRequest createHttpRequest(final InputStream inputStream) throws IOException {
        InputStreamReader reader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String startLine = bufferedReader.readLine();

        String line;
        List<String> headers = new ArrayList<>();
        while (!(line = bufferedReader.readLine()).equals("")) {
            headers.add(line);
        }

        HttpRequestStartLine requestStartLine = HttpRequestStartLine.from(startLine);
        HttpRequestHeader httpRequestHeader = HttpRequestHeader.from(headers);

        if (!httpRequestHeader.contains("Content-Length")) {
            return HttpRequest.of(requestStartLine, httpRequestHeader);
        }

        int contentLength = Integer.parseInt(httpRequestHeader.getValue("Content-Length"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);

        HttpRequestBody httpRequestBody = new HttpRequestBody(requestBody);

        return HttpRequest.of(requestStartLine, httpRequestHeader, httpRequestBody);
    }
}
