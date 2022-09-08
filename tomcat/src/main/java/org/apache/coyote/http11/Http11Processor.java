package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.HandlerMapping;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestFactory;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.util.FileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream();
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {

            HttpResponse httpResponse = getResponse(HttpRequestFactory.create(reader));
            String response = httpResponse.parseToString();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse getResponse(HttpRequest request) throws IOException {
        try {
            if (request.isStaticFileRequest()) {
                return getStaticResourceResponse(request);
            }
            return getDynamicResourceResponse(request);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return getNotFoundResponse(request);
        }
    }

    private HttpResponse getStaticResourceResponse(HttpRequest request) {
        Optional<String> extension = request.getExtension();
        if (extension.isPresent()) {
            ContentType contentType = ContentType.from(extension.get());
            return new HttpResponse().addProtocol(request.getRequestLine().getProtocol())
                    .addStatus(HttpStatus.OK)
                    .addResponseBody(getStaticResourceResponse(request.getRequestLine().getUri().getPath()),
                            contentType);
        }
        return getNotFoundResponse(request);
    }

    private HttpResponse getNotFoundResponse(HttpRequest request) {
        return getNotFoundResponse(new HttpResponse().addProtocol(request.getRequestLine().getProtocol())
                .addStatus(HttpStatus.NOT_FOUND)
                .addResponseBody("페이지를 찾을 수 없습니다.", ContentType.TEXT_HTML_CHARSET_UTF_8));
    }

    private HttpResponse getNotFoundResponse(HttpResponse request) {
        return request;
    }

    private String getStaticResourceResponse(String resourcePath) {
        return FileReader.readStaticFile(resourcePath, this.getClass());
    }

    private HttpResponse getDynamicResourceResponse(HttpRequest request) throws Exception {
        String path = request.getRequestLine().getUri().getPath();
        Controller controller = HandlerMapping.findController(path);
        return controller.service(request);
    }
}
