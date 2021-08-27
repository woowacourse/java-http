package nextstep.jwp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final Controller controller = new Controller();

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                final OutputStream outputStream = connection.getOutputStream()) {

            String firstLine = reader.readLine();
            if (firstLine == null) {
                return;
            }
            String[] firstTokens = firstLine.split(" ");
            String method = firstTokens[0];
            String uri = firstTokens[1];

            Map<String, List<String>> headers = new HashMap<>();

            String line = reader.readLine();
            while (!"".equals(line)) {
                String[] splits = line.split(": ", 2);
                String key = splits[0];
                List<String> values = Stream.of(splits[1].split(",")).map(String::trim).collect(Collectors.toList());
                headers.put(key, values);
                line = reader.readLine();
            }

            String response = "";

            if (uri.equals("/index.html")) {
                response = controller.index();
            }

            if (uri.startsWith("/login")) {
                log.debug("/login Request with uri {}", uri);
                response = controller.login(uri);
            }

            log.debug("outputStream => {}", response);
            outputStream.write(response.getBytes());
            outputStream.flush();
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
