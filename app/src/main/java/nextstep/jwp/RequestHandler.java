package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
            final HttpRequest httpRequest = HttpRequest.of(requestAsString(bufferedReader));

            String requestedResource = httpRequest.getRequestLine().toResource();


            final String htmlExtension = ".html";

            if (Objects.equals(requestedResource, "login")) {
                requestedResource = requestedResource.concat(htmlExtension);
            }

            if (requestedResource.contains("?")) {
                final Map<String, String> queryInfo = extractQuery(httpRequest.getRequestLine().toQueryString());
                final User user = InMemoryUserRepository.findByAccount(queryInfo.get("account"))
                        .orElseThrow(RuntimeException::new);
                if (user.checkPassword(queryInfo.get("password"))) {
                    log.info("Login successful!");
                } else {
                    log.info("Login failed");
                }
            }

            final URL resource = getClass().getClassLoader().getResource("static/" + requestedResource);
            final Path path = Paths.get(resource.getPath());
            final byte[] bytes = Files.readAllBytes(path);
            final HttpResponse response = new HttpResponse(bytes);

            outputStream.write(response.toBytes());
            outputStream.flush();
            outputStream.write(bytes);
            outputStream.flush();
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
            request.add(bufferedReader.readLine());
        }
        return request;
    }

    private Map<String, String> extractQuery(String queryString) {
        final Map<String, String> queryInfo = new HashMap<>();
        final String[] queries = queryString.split("&");
        for (String query : queries) {
            final int index = query.indexOf('=');
            final String key = query.substring(0, index);
            final String value = query.substring(index + 1);
            queryInfo.put(key, value);
        }
        return queryInfo;
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
