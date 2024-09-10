package org.apache.coyote.http11;

import com.techcourse.controller.RequestMapping;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.util.StaticResourceManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.catalina.SessionManager;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.common.Constants;
import org.apache.coyote.http11.common.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequestMessageReader;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.MediaType;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final SessionManager sessionManager = new SessionManager();

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

            HttpRequest request = HttpRequestMessageReader.read(inputStream);
            HttpResponse response = new HttpResponse(HttpStatusCode.OK);
            createResponse(request, response);


            writeResponse(outputStream, response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
        log.info("현재 세션: {}", sessionManager.getSessions());
    }

    private void createResponse(HttpRequest request, HttpResponse response) {
        if (StaticResourceManager.isExist("static" + request.getPath())) {
            createStaticResourceResponse(request, response);
            return;
        }
        Controller controller = RequestMapping.getController(request);
        controller.service(request, response);
    }

    private void createStaticResourceResponse(HttpRequest request, HttpResponse response) {
        String path = request.getPath();
        int extensionSeparatorIndex = path.lastIndexOf(".");
        String requestedExtension = extensionSeparatorIndex == -1 ? "" : path.substring(extensionSeparatorIndex + 1);
        MediaType mediaType = MediaType.fromExtension(requestedExtension);
        log.info("Requested MediaType: {}", mediaType);

        String responseBody = StaticResourceManager.read("static" + request.getPath());
        response.setStatus(HttpStatusCode.OK)
                .addHeader("Content-Type", mediaType.getValue())
                .setBody(responseBody);
    }

    private void writeResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        outputStream.write(response.getAsBytes());
        outputStream.flush();
    }
}
