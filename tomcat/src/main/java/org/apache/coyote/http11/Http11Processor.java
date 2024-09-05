package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             final var outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = new HttpRequest(reader);
            HttpResponse httpResponse = new HttpResponse();

            String path = httpRequest.getRequestLine().getPath();
            String accept = httpRequest.getHeaders().get("Accept");
            log.info("request accept: {}", accept);

            if (path.equals("/")) {
                httpResponse.setResponseBodyByText("Hello world!");
                httpResponse.setStatus200();
                httpResponse.setContentTypeHtml();

            } else if (path.contains("/login")) {
                URL resource = getClass().getClassLoader().getResource("static/login.html");
                httpResponse.setResponseBodyByPath(Path.of(resource.toURI()));
                httpResponse.setStatus200();
                httpResponse.setContentTypeHtml();
                log.info("response resource : {}", resource.toURI());

            } else {

                URL resource = getClass().getClassLoader().getResource("static" + path);
                log.info("response resource : {}", resource);

                if (resource == null) {
                    URL notFoundResource = getClass().getClassLoader().getResource("static/404.html");
                    httpResponse.setResponseBodyByPath(Path.of(resource.toURI()));
                    httpResponse.setStatus404();
                    httpResponse.setContentTypeHtml();
                    log.info("response resource : {}", notFoundResource.toURI());

                } else if (accept != null && accept.contains("text/css")) {
                    httpResponse.setResponseBodyByPath(Path.of(resource.toURI()));
                    httpResponse.setStatus200();
                    httpResponse.setContentTypeCss();
                    log.info("response resource : {}", resource.toURI());

                } else {
                    httpResponse.setResponseBodyByPath(Path.of(resource.toURI()));
                    httpResponse.setStatus200();
                    httpResponse.setContentTypeHtml();
                    log.info("response resource : {}", resource.toURI());

                }
            }

            final String response = httpResponse.getResponse();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }
}
