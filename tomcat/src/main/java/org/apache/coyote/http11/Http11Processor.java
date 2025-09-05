package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

    static class HttpResponse {

        private final String contentType;
        private final String responseBody;

        public HttpResponse(String contentType, String responseBody) {
            this.contentType = contentType;
            this.responseBody = responseBody;
        }

        public String serveResponse() {
            return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/" + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                responseBody);
        }
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
        if (httpRequest.getUri().equals("/")) {
            return new HttpResponse("html", "Hello world!");
        }

        if (httpRequest.getUri().startsWith("/login")) {
            Map<String, String> queryStrings = httpRequest.getQueryStrings();
            String account = queryStrings.get("account");

            Optional<User> user = InMemoryUserRepository.findByAccount(account);
            log.info("{}", user.orElse(null));

            return new HttpResponse(
                "html",
                Arrays.toString(staticFileLoader.readAllFileWithUri(httpRequest.getUri())) + "html"
            );
        }

        int extensionIndex = httpRequest.getUri().lastIndexOf(".");
        String extension = httpRequest.getUri().substring(extensionIndex + 1);

        String staticFile = new String(staticFileLoader.readAllFileWithUri(httpRequest.getUri()));
        return new HttpResponse(extension, staticFile);
    }
}
