package nextstep.jwp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Objects;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.ResponseBody;
import nextstep.jwp.view.View;
import nextstep.jwp.view.ViewResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrontController implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(FrontController.class);

    private final Socket connection;

    public FrontController(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}",
            connection.getInetAddress(),
            connection.getPort());

        try (
            final InputStream inputStream = connection.getInputStream();
            final OutputStream outputStream = connection.getOutputStream();
            final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream))
        ) {
            HttpRequest request = new HttpRequest(bufferedReader);
            HttpResponse response = new HttpResponse();

            Controller handler = request.getHandler();
            View view = handler.handle(request, response);
            ViewResolver viewResolver = view.resolve();

            response.setBody(new ResponseBody(
                Files.readAllBytes(new File(viewResolver.getFilePath()).toPath())));

            response.write(outputStream);
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
