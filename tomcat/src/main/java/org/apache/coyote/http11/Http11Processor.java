package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.Processor;
import com.techcourse.handler.DefaultHandler;
import org.apache.coyote.HttpRequestHandler;
import com.techcourse.handler.LoginHandler;
import com.techcourse.handler.RootHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final String PROTOCOL = "HTTP/1.1";
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Map<String, HttpRequestHandler> handlerMap;
    private final HttpRequestHandler defaultHandler;
    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.handlerMap = new HashMap<>();
        handlerMap.put("/", new RootHandler());
        handlerMap.put("/login", new LoginHandler());
        this.defaultHandler = new DefaultHandler();
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

            final HttpRequest request = parseRequest(inputStream);
            final HttpResponse response = new HttpResponse(PROTOCOL);

            if (handlerMap.containsKey(request.getPath())) {
                handleRequest(request, response);
                writeResponse(response, outputStream);
                return;
            }

            defaultHandler.handleGet(request, response);
            writeResponse(response, outputStream);

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void writeResponse(final HttpResponse response, OutputStream outputStream) {
        try {
            outputStream.write(response.getResponse().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleRequest(HttpRequest request, HttpResponse response){
        final HttpRequestHandler handler = handlerMap.get(request.getPath());
        switch (request.getMethod()) {
            case "GET" -> handler.handleGet(request, response);
            default -> throw new UnsupportedOperationException("지원하지 않는 요청 방식입니다.");
        }
    }

    private HttpRequest parseRequest(InputStream inputStream){
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final String requestLine = bufferedReader.readLine();
            if(requestLine == null || requestLine.isBlank()){
                throw new IllegalArgumentException("요청 형식이 올바르지 않습니다.");
            }
            final StringTokenizer tokenizer = new StringTokenizer(requestLine);
            String method = tokenizer.nextToken();
            String pathInfo = tokenizer.nextToken();
            String protocol = tokenizer.nextToken();

            String path = pathInfo;
            Map<String, String> params = new HashMap<>();

            int queryIndex = pathInfo.indexOf('?');
            if(queryIndex != -1){
                path = pathInfo.substring(0, queryIndex);
                String queryString = pathInfo.substring(queryIndex + 1);

                String[] param = queryString.split("&");
                for (String pair : param) {
                    int equalIndex = pair.indexOf('=');
                    if (equalIndex != -1) {
                        String key = pair.substring(0, equalIndex);
                        String value = pair.substring(equalIndex + 1);

                        params.put(key, value);
                    }
                }
            }
            return new HttpRequest(method, path, params, protocol);
        } catch (IOException e){
            throw new UncheckedServletException(e);
        }
    }
}
