package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.http.HttpHeader;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.reqeust.HttpRequestLine;
import nextstep.jwp.io.ClassPathResource;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(inputStream));

            HttpRequest httpRequest = createHttpRequest(bufferReader);
            String httpResponse = createHttpResponse(httpRequest);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest createHttpRequest(final BufferedReader bufferReader) throws IOException {
        return new HttpRequest(httpRequestLine(bufferReader), httpRequestHeader(bufferReader));
    }

    private HttpRequestLine httpRequestLine(final BufferedReader bufferReader) throws IOException {
        String requestLine = bufferReader.readLine();
        return HttpRequestLine.from(requestLine);
    }

    private HttpHeader httpRequestHeader(final BufferedReader bufferReader) throws IOException {
        List<String> requestHeaders = new ArrayList<>();
        while (bufferReader.readLine().isBlank()) {
            requestHeaders.add(bufferReader.readLine());
        }
        return new HttpHeader(requestHeaders);
    }

    private String createHttpResponse(final HttpRequest httpRequest) {
        String url = httpRequest.getUrl();
        if (url.equals("/")) {
            return createRootResponse(httpRequest);
        }
        String responseBody = new ClassPathResource(url).getFileContents();
        return createOkResponse(httpRequest.getContentType(), responseBody);
    }

    private String createRootResponse(final HttpRequest httpRequest) {
        String responseBody = "Hello world!";
        return createOkResponse(httpRequest.getContentType(), responseBody);
    }

    private String createOkResponse(final String contentType, final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
