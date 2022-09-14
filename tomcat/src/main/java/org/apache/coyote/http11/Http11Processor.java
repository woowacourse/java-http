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
import nextstep.jwp.controller.ControllerMapping;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.view.View;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestFactory;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
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
            return getNotFoundResponse();
        }
    }

    private HttpResponse getStaticResourceResponse(HttpRequest request) {
        Optional<String> extension = request.getExtension();
        if (extension.isPresent()) {
            ContentType contentType = ContentType.from(extension.get());
            return HttpResponse.ok()
                    .addResponseBody(getStaticResourceResponse(request.getPath()), contentType)
                    .build();
        }
        return getNotFoundResponse();
    }

    private String getStaticResourceResponse(String resourcePath) {
        return FileReader.readStaticFile(resourcePath, this.getClass());
    }

    private HttpResponse getDynamicResourceResponse(HttpRequest request) throws Exception {
        Controller controller = ControllerMapping.findController(request.getPath());
        return controller.service(request);
    }

    private HttpResponse getNotFoundResponse() {
        return HttpResponse.notFound()
                .addResponseBody(View.NOT_FOUND.getContents(), ContentType.TEXT_HTML_CHARSET_UTF_8)
                .build();
    }
}
