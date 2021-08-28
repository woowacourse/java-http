package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.network.HttpRequest;
import nextstep.jwp.network.HttpResponse;
import nextstep.jwp.network.HttpStatus;
import nextstep.jwp.network.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final List<String> requestAsString = requestAsString(bufferedReader);
            if (requestAsString.isEmpty()) {
                return;
            }

            final HttpRequest httpRequest = HttpRequest.of(requestAsString);
            HttpResponse httpResponse;

            final URI uri = httpRequest.toURI();

            final Map<String, Controller> controllers = new HashMap<>();
            final Controller loginController = new LoginController("/login");
            controllers.put(loginController.getResource(), loginController);
            final ControllerMapping controllerMapping = new ControllerMapping(controllers);

            final Controller foundController = controllerMapping.findByResource(uri.getPath());
            httpResponse = foundController.doGet(httpRequest);

            final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(httpResponse.asString());
            bufferedWriter.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private List<String> requestAsString(BufferedReader bufferedReader) throws IOException {
        final List<String> request = new ArrayList<>();
        while (bufferedReader.ready()) {
            final String line = bufferedReader.readLine();
            if (line == null) {
                continue;
            }
            request.add(line);
        }
        return request;
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
