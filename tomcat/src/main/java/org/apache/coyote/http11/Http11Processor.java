package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestCreator;
import org.apache.coyote.http11.response.StaticFileResponseUtils;
import org.apache.coyote.http11.response.ViewResponseUtils;
import org.apache.coyote.http11.response.view.View;
import org.apache.coyote.http11.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final View NOT_FOUND_RESPONSE_VIEW = View.createByStaticResource("/404.html");

    private final Socket connection;
    private final ServletContainer servletContainer;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.servletContainer = new ServletContainer();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            HttpRequest request = createHttpRequest(inputStream);
            String response = response(request);
            writeResponse(outputStream, response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest createHttpRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return HttpRequestCreator.createHttpRequest(reader);
    }

    private String response(HttpRequest request) throws IOException {
        Optional<View> view = servletContainer.service(request);
        if (view.isPresent()) {
            return ViewResponseUtils.createResponse(view.get());
        }
        String filePath = request.getPath();
        if (StaticFileResponseUtils.isExistFile(filePath)) {
            return StaticFileResponseUtils.createResponse(filePath);
        }
        return ViewResponseUtils.createResponse(NOT_FOUND_RESPONSE_VIEW);
    }

    private void writeResponse(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
