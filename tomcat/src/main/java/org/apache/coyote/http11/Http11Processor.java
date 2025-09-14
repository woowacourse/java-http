package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.controller.BasicController;
import org.apache.controller.Controller;
import org.apache.controller.LoginController;
import org.apache.controller.NotFoundController;
import org.apache.controller.RegisterController;
import org.apache.controller.StaticController;
import org.apache.coyote.Processor;
import org.apache.http.HttpCookie;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final List<Controller> controllers = List.of(
            new BasicController(),
            new LoginController(),
            new RegisterController(),
            new StaticController()
    );

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

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest httpRequest = new HttpRequest(bufferedReader);

            Controller processorableController = controllers.stream()
                    .filter(controller -> controller.isProcessable(httpRequest))
                    .findFirst()
                    .orElse(new NotFoundController());

            HttpResponse httpResponse = processorableController.process(httpRequest);

            String response = makeResponse(httpRequest, httpResponse);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String makeResponse(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {

        final String contentType = httpRequest.getMineType() + ";charset=utf-8";
        final String protocol = httpRequest.getProtocol();
        final HttpStatus httpStatus = httpResponse.getHttpStatus();
        final String responseBody = httpResponse.getResponseBody();
        final HttpCookie httpCookie = httpResponse.getHttpCookie();
        final String statusLine = protocol + " " + httpStatus.getCode() + " " + httpStatus.getCodeName() + " ";

        StringBuilder headers = new StringBuilder();
        headers.append("Content-Type: ").append(contentType).append(" \r\n");
        headers.append("Content-Length: ").append(responseBody.getBytes().length).append(" \r\n");

        if (httpCookie != null) {
            headers.append("Set-Cookie: ").append(httpCookie.getKeyAndJSessionID()).append("\r\n");
        }

        if (httpStatus == HttpStatus.FOUND) {
            headers.append(httpResponse.getLocationHeader());
        }

        return String.join("\r\n",
                statusLine,
                headers.toString(),
                responseBody);
    }
}
