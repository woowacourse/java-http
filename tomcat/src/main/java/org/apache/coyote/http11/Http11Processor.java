package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import org.apache.coyote.Processor;
import org.apache.coyote.common.HttpCookie;
import org.apache.coyote.common.HttpHeaders;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.common.RequestUri;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.util.CookieParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping;
    private final ExceptionHandler exceptionHandler;

    public Http11Processor(Socket connection, RequestMapping requestMapping, ExceptionHandler exceptionHandler) {
        this.connection = connection;
        this.requestMapping = requestMapping;
        this.exceptionHandler = exceptionHandler;
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
            HttpRequest request = createHttpRequest(inputStream);
            HttpResponse response = new HttpResponse();
            handle(request, response);
            outputStream.write(response.toBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest createHttpRequest(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        RequestUri requestUri = getRequestUri(bufferedReader);
        HttpHeaders httpHeaders = getHttpHeaders(bufferedReader);
        HttpCookie cookie = getCookie(httpHeaders);
        String requestBody = getRequestBody(httpHeaders, bufferedReader);
        return new HttpRequest(requestUri, httpHeaders, cookie, requestBody);
    }

    private RequestUri getRequestUri(BufferedReader bufferedReader) throws IOException {
        return RequestUri.from(bufferedReader.readLine());
    }

    private HttpHeaders getHttpHeaders(BufferedReader bufferedReader) throws IOException {
        HttpHeaders httpHeaders = new HttpHeaders();
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null || line.isBlank()) {
                break;
            }
            String[] header = line.split(":");
            String key = header[0].strip();
            List<String> values = Arrays.asList(header[1].strip().split(","));
            httpHeaders.addHeader(key, values);
        }
        return httpHeaders;
    }

    private HttpCookie getCookie(HttpHeaders httpHeaders) {
        return CookieParser.parse(httpHeaders.getHeader("Cookie"));
    }

    private String getRequestBody(HttpHeaders httpHeaders, BufferedReader bufferedReader) throws IOException {
        String header = httpHeaders.getHeader("Content-Length");
        if (header.isBlank()) {
            return "";
        }
        int contentLength = Integer.parseInt(header);
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private void handle(HttpRequest request, HttpResponse response) throws Exception {
        Controller controller = requestMapping.getController(request);
        try {
            controller.service(request, response);
        } catch (Exception e) {
            exceptionHandler.handle(request, response, e);
        }
    }
}
