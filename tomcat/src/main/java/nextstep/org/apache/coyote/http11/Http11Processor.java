package nextstep.org.apache.coyote.http11;

import static nextstep.org.apache.coyote.http11.HttpUtil.selectFirstContentTypeOrDefault;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.org.apache.coyote.Processor;
import nextstep.org.apache.coyote.http11.servlet.Context;
import nextstep.org.apache.coyote.http11.servlet.Servlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String RESOURCES_PATH_PREFIX = "static";
    private static final String INTERNAL_SERVER_ERROR_DEFAULT_MESSAGE = "500 Internal Server Error";

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
        try (
                InputStream inputStream = connection.getInputStream();
                OutputStream outputStream = connection.getOutputStream()
        ) {
            Http11Request request = new Http11Request(inputStream);
            Http11Response response = new Http11Response(Status.OK);

            Context servletContainer = new Context();
            Servlet servlet = servletContainer.getServlet(request.getPathInfo());
            try {
                servlet.service(request, response);
            } catch (Exception e) {
                responseInternalServerError(request, response);
            }

            writeResponse(outputStream, response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void responseInternalServerError(Http11Request request, Http11Response response)
            throws IOException {
        String internalServerErrorPageBody = createResponseBody("/500.html")
                .orElse(INTERNAL_SERVER_ERROR_DEFAULT_MESSAGE);
        String contentType = selectFirstContentTypeOrDefault(request.getHeader("Accept"));
        response.setStatus(Status.INTERNAL_SERVER_ERROR)
                .setHeader("Content-Type", contentType + ";charset=utf-8")
                .setHeader("Content-Length", String.valueOf(
                        internalServerErrorPageBody.getBytes(
                                StandardCharsets.UTF_8).length))
                .setBody(internalServerErrorPageBody);
    }

    private void writeResponse(OutputStream outputStream, Http11Response response)
            throws IOException {
        outputStream.write(response.createResponseAsByteArray());
        outputStream.flush();
    }

    private Optional<String> createResponseBody(String requestPath) throws IOException {
        if (requestPath.equals("/")) {
            return Optional.of("Hello world!");
        }

        String resourceName = RESOURCES_PATH_PREFIX + requestPath;
        if (!resourceName.contains(".")) {
            resourceName += ".html";
        }
        URL resource = getClass().getClassLoader().getResource(resourceName);

        if (Objects.isNull(resource)) {
            return Optional.empty();
        }
        return Optional.of(new String(Files.readAllBytes(new File(resource.getFile()).toPath())));
    }
}
