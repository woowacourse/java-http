package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.request.requestLine.HttpRequestLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ResourceProcessor resourceProcessor;
    private final ApiProcessor apiProcessor;

    public Http11Processor(final Socket connection) {
        this.resourceProcessor = new ResourceProcessor();
        this.apiProcessor = new ApiProcessor();
        this.connection = connection;
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
            HttpRequest httpRequest = getHttpRequest(inputStream);

            RequestPathType requestPathType = httpRequest.getRequestPathType();
            if (requestPathType.isAPI()) {
                apiProcessor.process(connection, httpRequest);
            }
            if (requestPathType.isResource()) {
                resourceProcessor.process(connection, httpRequest.getRequestPath());
            }

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest getHttpRequest(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        HttpRequestLine httpRequestLine = HttpRequestLine.toHttpRequestLine(br.readLine());
        HttpRequestHeader httpRequestHeader = getHttpRequestHeader(br);

        HttpRequestBody httpRequestBody = null;
        if (httpRequestLine.getMethodType().isPost()) {
            httpRequestBody = getHttpRequestBody(httpRequestHeader, br);
        }

        return new HttpRequest(httpRequestLine, httpRequestHeader, httpRequestBody);
    }

    private HttpRequestBody getHttpRequestBody(HttpRequestHeader httpRequestHeader, BufferedReader br)
            throws IOException {
        int contentLength = Integer.parseInt(httpRequestHeader.getContentLength());
        char[] buffer = new char[contentLength];
        br.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        return HttpRequestBody.toHttpRequestBody(requestBody, httpRequestHeader.getContentType());
    }

    private HttpRequestHeader getHttpRequestHeader(BufferedReader br) throws IOException {
        List<String> requestHeaderLines = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null && !line.equals("")) {
            requestHeaderLines.add(line);
        }
        return HttpRequestHeader.toHttpRequestHeader(requestHeaderLines);
    }
}
