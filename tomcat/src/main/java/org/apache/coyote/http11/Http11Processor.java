package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.AbstractHandler;
import org.apache.coyote.http11.handler.HelloHandler;
import org.apache.coyote.http11.handler.LoginHandler;
import org.apache.coyote.http11.handler.StaticResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {
            HttpRequest httpRequestData = createHttpRequestData(inputStream);
            String httpResponse = createHttpResponseMessage(responseResource(httpRequestData));

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest createHttpRequestData(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String requestLine = bufferedReader.readLine();
        List<String> headerTokens = new ArrayList<>();

        String nowLine = bufferedReader.readLine();
        while (!nowLine.isBlank()) {
            if (nowLine.startsWith("Accept")) {
                log.info(nowLine);
            }

            headerTokens.add(nowLine);
            nowLine = bufferedReader.readLine();
        }

        return new HttpRequest(requestLine, new Header(headerTokens));
    }

    private HttpResponse responseResource(HttpRequest httpRequest) throws IOException {
        AbstractHandler helloHandler = new HelloHandler();
        AbstractHandler staticResourceHandler = new StaticResourceHandler();
        AbstractHandler loginHandler = new LoginHandler();

        List<AbstractHandler> handlers = List.of(helloHandler, staticResourceHandler, loginHandler);
        AbstractHandler targetHandler = handlers.stream()
                .filter(it -> it.canHandle(httpRequest))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        return targetHandler.handle(httpRequest);
    }

    private String createHttpResponseMessage(HttpResponse httpResponseData) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + httpResponseData.contentType() + " ",
                "Content-Length: " + httpResponseData.responseBody().length + " ",
                "",
                new String(httpResponseData.responseBody(), StandardCharsets.UTF_8));
    }
}
