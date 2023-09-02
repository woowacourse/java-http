package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.Handler;
import org.apache.coyote.common.HttpHeaders;
import org.apache.coyote.common.HttpRequest;
import org.apache.coyote.common.HttpResponse;
import org.apache.coyote.common.RequestUri;
import org.apache.coyote.http11.handler.IndexHandler;
import org.apache.coyote.http11.handler.LoginHandler;
import org.apache.coyote.http11.handler.StaticResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Map<String, Handler> handlerMap = new HashMap<>();

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        handlerMap.put("/", new IndexHandler());
        handlerMap.put("/login", new LoginHandler());
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
            HttpResponse response = handle(request);
            outputStream.write(response.toBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest createHttpRequest(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        RequestUri requestUri = getRequestUri(bufferedReader);
        HttpHeaders httpHeaders = getHttpHeaders(bufferedReader);
        return new HttpRequest(requestUri, httpHeaders);
    }

    private RequestUri getRequestUri(BufferedReader bufferedReader) throws IOException {
        String requestUri = bufferedReader.readLine();
        log.info("requestUri: {}", requestUri);
        return RequestUri.from(requestUri);
    }

    private HttpHeaders getHttpHeaders(BufferedReader bufferedReader) throws IOException {
        HttpHeaders httpHeaders = new HttpHeaders();
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null || line.isEmpty()) {
                break;
            }
            String[] header = line.split(":");
            String key = header[0].trim();
            List<String> values = Arrays.asList(header[1].trim().split(","));
            httpHeaders.addHeader(key, values);
        }
        return httpHeaders;
    }

    private HttpResponse handle(HttpRequest request) throws IOException {
        Handler handler = handlerMap.getOrDefault(request.getPath(), StaticResourceHandler.INSTANCE);
        return handler.handle(request);
    }
}
