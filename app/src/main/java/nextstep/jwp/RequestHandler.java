package nextstep.jwp;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
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
            byte[] bytes = "".getBytes();
            if (requestAsString.isEmpty()) {
                return;
            }

            final HttpRequest httpRequest = HttpRequest.of(requestAsString);
            HttpResponse httpResponse = new HttpResponse(HttpStatus.OK, bytes);

            final URI uri = httpRequest.toURI();

            String requestedResource = uri.getPath();

            final String htmlExtension = ".html";

            if ("/login".equals(requestedResource)) {
                if (!httpRequest.toURI().hasQuery()) {
                    requestedResource = requestedResource.concat(htmlExtension);
                    URL resource = getClass().getClassLoader().getResource("static" + requestedResource);
                    Path path = Paths.get(resource.getPath());
                    bytes = Files.readAllBytes(path);

                    httpResponse = new HttpResponse(HttpStatus.OK, bytes);
                } else {
                    final Map<String, String> queryInfo = extractQuery(uri.getQuery());
                    final User user = InMemoryUserRepository.findByAccount(queryInfo.get("account"))
                            .orElseThrow(() -> new UserNotFoundException(queryInfo.get("account")));
                    if (user.checkPassword(queryInfo.get("password"))) {
                        log.info("Login successful!");

                        URL resource = getClass().getClassLoader().getResource("static" + "/index.html");
                        Path path = Paths.get(resource.getPath());
                        bytes = Files.readAllBytes(path);

                        httpResponse = new HttpResponse(HttpStatus.FOUND, bytes);
                    } else {
                        log.info("Login failed");

                        URL resource = getClass().getClassLoader().getResource("static" + "/401.html");
                        Path path = Paths.get(resource.getPath());
                        bytes = Files.readAllBytes(path);

                        httpResponse = new HttpResponse(HttpStatus.UNAUTHORIZED, bytes);
                    }

                }
            }
            if ("/".equals(requestedResource)) {
                bytes = "Hello world!".getBytes();

                httpResponse = new HttpResponse(HttpStatus.OK, bytes);
            }
            if ("/index.html".equals(requestedResource)) {
                URL resource = getClass().getClassLoader().getResource("static" + requestedResource);
                Path path = Paths.get(resource.getPath());
                bytes = Files.readAllBytes(path);

                httpResponse = new HttpResponse(HttpStatus.OK, bytes);
            }

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
