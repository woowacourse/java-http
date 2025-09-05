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
    private static final String STATIC_FILE_PREFIX = "static";

    private static final StaticFileLoader staticFileLoader = new StaticFileLoader();

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    static class StaticFileLoader {
        private static final String PREFIX = "static";

        public File findFileWithUri(final String fileUri) {
            URL systemResource = ClassLoader.getSystemResource(PREFIX + fileUri);
            if (systemResource == null) {
                throw new IllegalArgumentException();
            }

            return new File(systemResource.getFile());
        }

        public byte[] readAllFileWithUri(final String fileUri) throws IOException {
            return Files.readAllBytes(staticFileLoader.findFileWithUri(fileUri).toPath());
        }
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String inputLine = bufferedReader.readLine();

            HttpRequest httpRequest = new HttpRequest(inputLine.split(" ")[0], inputLine.split(" ")[1]);

            String response = null;
            if (httpRequest.getUri().startsWith("/login")) {
                Map<String, String> queryStrings = httpRequest.getQueryStrings();
                String account = queryStrings.get("account");

                Optional<User> user = InMemoryUserRepository.findByAccount(account);
                log.info("{}", user.orElse(null));

                response = getHttpResponse(
                    "html",
                    Arrays.toString(staticFileLoader.readAllFileWithUri(httpRequest.getUri())) + "html"
                );
            } else if (httpRequest.getUri().contains(".")) {
                int extensionIndex = httpRequest.getUri().lastIndexOf(".");
                String extension = httpRequest.getUri().substring(extensionIndex + 1);

                if (extension.equals("html") || extension.equals("css") || extension.equals("js")) {
                    String staticFile = new String(staticFileLoader.readAllFileWithUri(httpRequest.getUri()));
                    response = getHttpResponse(extension, staticFile);
                }
            } else {
                response = getHttpResponse("html", "Hello world!");
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getHttpResponse(final String contentType, final String responseBody) {
        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/" + contentType + ";charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
            "",
            responseBody);
    }
}
