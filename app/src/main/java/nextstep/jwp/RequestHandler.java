package nextstep.jwp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.ControllerMapper;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(final Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        LOG.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
            final OutputStream outputStream = connection.getOutputStream()) {
            HttpRequest httpRequest = new HttpRequest(
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
            );

            HttpResponse httpResponse = new HttpResponse(outputStream);
            Controller controller = ControllerMapper.getControllerByUrl(httpRequest.getUrl());

            if (controller == null) {
                httpResponse.transfer(httpRequest.getUrl());
            } else {
                controller.service(httpRequest, httpResponse);
            }
//            } else if (httpRequest.getHttpMethod().equals(HttpMethod.GET)) {
//                controller.get(httpRequest, httpResponse);
//            } else if (httpRequest.getHttpMethod().equals(HttpMethod.POST)) {
//                controller.post(httpRequest, httpResponse);
//            }

        } catch (IOException exception) {
            LOG.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            LOG.error("Exception closing socket", exception);
        }
    }
}
