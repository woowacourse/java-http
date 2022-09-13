package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeaders;
import org.apache.coyote.http11.request.StartLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection, RequestMapping requestMapping) {
        this.connection = connection;
        this.requestMapping = requestMapping;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
             final BufferedWriter bufferedWriter = new BufferedWriter(
                     new OutputStreamWriter(connection.getOutputStream()))) {

            HttpRequest httpRequest = new HttpRequest();
            HttpResponse httpResponse = new HttpResponse();

            processRequest(bufferedReader, httpRequest, httpResponse);
            Controller controller = requestMapping.findController(httpRequest.getPath());
            controller.service(httpRequest, httpResponse);

            bufferedWriter.write(httpResponse.asString());
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void processRequest(BufferedReader bufferedReader, HttpRequest httpRequest, HttpResponse httpResponse)
            throws IOException {
        setStartLine(httpRequest, bufferedReader);
        setRequestHeaders(httpRequest, httpResponse, bufferedReader);
        setSession(httpRequest, httpResponse);
        setRequestBody(httpRequest, bufferedReader);
    }

    private void setSession(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpRequest.setSession();
        RequestHeaders requestHeaders = httpRequest.getRequestHeaders();
        if (requestHeaders.doesNeedToSetJSessionIdCookie()) {
            httpResponse.setJSessionCookie(requestHeaders.getJSessionId());
        }
    }

    private void setStartLine(HttpRequest httpRequest, BufferedReader bufferedReader) throws IOException {
        StartLine startLine = new StartLine(bufferedReader.readLine());
        httpRequest.setStartLine(startLine);
    }

    private void setRequestHeaders(HttpRequest httpRequest, HttpResponse httpResponse, BufferedReader bufferedReader)
            throws IOException {
        RequestHeaders requestHeaders = RequestHeaders.of(readRequestHeaders(bufferedReader));
        httpRequest.setRequestHeaders(requestHeaders);
    }


    private static List<String> readRequestHeaders(BufferedReader bufferedReader) throws IOException {
        String line;
        List<String> requestHeaders = new ArrayList<>();
        while (!(line = bufferedReader.readLine()).isEmpty()) {
            requestHeaders.add(line);
        }
        return requestHeaders;
    }

    private void setRequestBody(HttpRequest httpRequest, BufferedReader bufferedReader) throws IOException {
        String rawRequestBody = readRequestBody(httpRequest.getRequestHeaders(), bufferedReader);
        RequestBody requestBody = RequestBody.of(rawRequestBody);
        httpRequest.setRequestBody(requestBody);
    }

    private String readRequestBody(RequestHeaders requestHeaders, BufferedReader bufferedReader)
            throws IOException {
        int contentLength = requestHeaders.getContentLength();
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String readRequestBody = new String(buffer);
        return URLDecoder.decode(readRequestBody, Charset.defaultCharset());
    }
}
