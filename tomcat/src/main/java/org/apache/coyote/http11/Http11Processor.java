package org.apache.coyote.http11;

import com.techcourse.controller.ApiRouter;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.util.StaticResourceManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.catalina.SessionManager;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.MediaType;
import org.apache.coyote.Processor;
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

            HttpRequest request = readRequest(inputStream);
            HttpResponse response = createResponse(request);

            writeResponse(outputStream, response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
        log.info("현재 세션: {}", sessionManager.getSessions());
    }

    private HttpRequest readRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\r\n");
            if (line.isEmpty()) {
                break;
            }
        }
        HttpRequest request = new HttpRequest(sb.toString());

        if (request.getContentLength() > 0) {
            char[] body = new char[request.getContentLength()];
            reader.read(body);
            request.setBody(new String(body));
        }
        log.info("Request: {}", request);

        return request;
    }

    private HttpResponse createResponse(HttpRequest request) {
        if (StaticResourceManager.isExist("static" + request.getPath())) {
            return createStaticResourceResponse(request);
        }
        return ApiRouter.route(request.getMethod(), request.getPath(), request);
    }

    private HttpResponse createStaticResourceResponse(HttpRequest request) {
        String path = request.getPath();
        int extensionSeparatorIndex = path.lastIndexOf(".");
        String requestedExtension = extensionSeparatorIndex == -1 ? "" : path.substring(extensionSeparatorIndex + 1);
        MediaType mediaType = MediaType.fromExtension(requestedExtension);
        log.info("Requested MediaType: {}", mediaType);

        String responseBody = StaticResourceManager.read("static" + request.getPath());
        return new HttpResponse(200, "OK")
                .addHeader("Content-Type", mediaType.getValue())
                .setBody(responseBody);
    }

    private void writeResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        outputStream.write(response.getAsBytes());
        outputStream.flush();
    }
}
