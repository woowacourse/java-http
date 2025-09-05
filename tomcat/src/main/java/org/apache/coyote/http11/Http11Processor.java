package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final StaticFileLoader staticFileLoader;

    public Http11Processor(final Socket connection, final StaticFileLoader staticFileLoader) {
        this.connection = connection;
        this.staticFileLoader = staticFileLoader;
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

            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String inputLine = bufferedReader.readLine();

            HttpResponse response = handle(new HttpRequest(inputLine.split(" ")[0], inputLine.split(" ")[1]));

            outputStream.write(response.serveResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handle(final HttpRequest httpRequest) throws IOException {
        if (httpRequest.uri().equals("/")) {
            return new HttpResponse("html", "Hello world!");
        }

        if (httpRequest.uri().startsWith("/login")) {
            Map<String, String> queryStrings = httpRequest.getQueryStrings();
            String account = queryStrings.get("account");

            Optional<User> user = InMemoryUserRepository.findByAccount(account);
            log.info("{}", user.orElse(null));

            return new HttpResponse(
                "html",
                Arrays.toString(staticFileLoader.readAllFileWithUri(httpRequest.uri())) + "html"
            );
        }

        int extensionIndex = httpRequest.uri().lastIndexOf(".");
        String extension = httpRequest.uri().substring(extensionIndex + 1);

        String staticFile = new String(staticFileLoader.readAllFileWithUri(httpRequest.uri()));
        return new HttpResponse(extension, staticFile);
    }
}
