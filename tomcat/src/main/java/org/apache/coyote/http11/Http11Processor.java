package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.ControllerMapper;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
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

            InputReader inputReader = new InputReader(inputStream);
            HttpRequest request = new HttpRequest(inputReader);

            Controller controller = ControllerMapper.map(request.getPath());
            HttpResponse response = controller.process(request);
            setResponse(request, response);

            outputStream.write(response.getResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void setResponse(HttpRequest request, HttpResponse response) throws IOException {
        String location = getLocation(request, response);
        String contentType = FileReader.probeContentType(location);
        response.setContentType(contentType);

        String responseBody = FileReader.read(location);
        response.setContentLength(responseBody.getBytes().length);
        response.setBody(responseBody);
    }

    private String getLocation(HttpRequest request, HttpResponse response) {
        if (response.hasLocation()) {
            return response.getLocation();
        }
        String path = request.getPath();
        if ("/".equals(path)) {
            return Constants.DEFAULT_URI;
        }
        if (!path.contains(".")) {
            return path + Constants.EXTENSION_OF_HTML;
        }
        return path;
    }
}
