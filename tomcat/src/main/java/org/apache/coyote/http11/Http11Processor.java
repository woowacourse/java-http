package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.apache.coyote.RequestMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_FILE_PREFIX = "static";

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
            String[] firstLine = bufferedReader.readLine().split(" ");

            String httpMethod = firstLine[0];
            String httpUri = firstLine[1];

            final var response = makeResponse(httpMethod, httpUri);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String makeResponse(final String httpMethod, final String httpUri) throws IOException {
        if (RequestMethod.valueOf(httpMethod) == RequestMethod.GET) {
            if (httpUri.contains("?")) {
                int index = httpUri.indexOf("?");
                String uri = httpUri.substring(0, index);
                String query = httpUri.substring(index + 1);

                String[] queryStrings = query.split("&");
                String account = queryStrings[0].split("=")[1];

                Optional<User> user = InMemoryUserRepository.findByAccount(account);
                log.info("{}", user.orElse(null));

                return getHttpResponse(
                    "html",
                    new String(Files.readAllBytes(new File(ClassLoader.getSystemResource(STATIC_FILE_PREFIX + uri + ".html").getFile()).toPath()))
                );
            } else if (httpUri.contains(".")) {
                String staticFile = new String(Files.readAllBytes(new File(ClassLoader.getSystemResource(STATIC_FILE_PREFIX + httpUri).getFile()).toPath()));
                return getHttpResponse(httpUri.substring(httpUri.indexOf(".") + 1), staticFile);
            }
        }

        return getHttpResponse("html", "Hello world!");
    }

    private static String getHttpResponse(final String contentType, final String responseBody) {
        return String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/" + contentType + ";charset=utf-8 ",
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }
}
