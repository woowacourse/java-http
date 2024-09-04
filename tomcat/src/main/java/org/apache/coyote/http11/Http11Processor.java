package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String GET_METHOD = "GET";
    private static final String DEFAULT_ROUTE = "/";
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";

    private final Socket connection;
    private final ContentTypeConverter contentTypeConverter = new ContentTypeConverter();
    private final ResponseBinder responseBinder = new ResponseBinder();

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
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            final String requestFirstLine = bufferedReader.readLine();

            if (GET_METHOD.equals(requestFirstLine.split(" ")[0])) {
                final var response = bindResponse(requestFirstLine);
                outputStream.write(response.getBytes());
            }
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String bindResponse(String requestFirstLine) throws IOException {
        String route = requestFirstLine.split(" ")[1];
        if (DEFAULT_ROUTE.equals(route)) {
            return responseBinder.buildSuccessfulResponse(DEFAULT_RESPONSE_BODY);
        }

        URL resource = getClass().getClassLoader().getResource("static" + route);
        if (resource == null) {
            URL badRequestURL = getClass().getClassLoader().getResource("static/404.html");
            return responseBinder.buildFailedResponse(404, "NotFound",
                    new String(Files.readAllBytes(new File(badRequestURL.getFile()).toPath())));
        }

        String staticResource = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        if (route.split("[.]").length == 0) {
            return responseBinder.buildSuccessfulResponse(staticResource);
        }
        String fileExtension = route.split("[.]")[1];
        String contentType = contentTypeConverter.mapToContentType(fileExtension);
        return responseBinder.buildSuccessfulResponse(contentType, staticResource);
    }
}
