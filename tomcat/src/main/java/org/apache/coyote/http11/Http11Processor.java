package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.controller.Controller;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.io.HttpResponseWriter;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.apache.coyote.http11.request.RequestMapping;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.requestMapping = new RequestMapping();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStreamReader = new InputStreamReader(connection.getInputStream());
             final var outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
             final BufferedReader reader = new BufferedReader(inputStreamReader)) {
            HttpRequest request = readHttpRequest(reader);
            HttpResponse response = HttpResponse.defaultResponse();
            Controller controller = requestMapping.getController(request);
            controller.service(request, response);
            HttpResponseWriter httpResponseWriter = new HttpResponseWriter(outputStreamWriter);
            httpResponseWriter.writeResponse(response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readHttpRequest(BufferedReader reader) throws IOException {
        HttpRequestLine httpRequestLine = new HttpRequestLine(reader.readLine());
        HttpRequestHeader httpRequestHeader = new HttpRequestHeader(readRequestHeaders(reader));
        HttpRequestBody httpRequestBody = new HttpRequestBody(readRequestBody(httpRequestHeader, reader));
        return new HttpRequest(httpRequestLine, httpRequestHeader, httpRequestBody);
    }

    private List<String> readRequestHeaders(BufferedReader reader) throws IOException {
        List<String> requestHeaders = new ArrayList<>();
        String line;
        while (!(line = reader.readLine()).isEmpty()) {
            requestHeaders.add(line);
        }
        return requestHeaders;
    }

    private String readRequestBody(HttpRequestHeader requestHeader, BufferedReader reader) throws IOException {
        String contentLengthHeader = requestHeader.getContentLength();
        if (contentLengthHeader == null) {
            return "";
        }
        int contentLength = Integer.parseInt(contentLengthHeader);
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }
}
