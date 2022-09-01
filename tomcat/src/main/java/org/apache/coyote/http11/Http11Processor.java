package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.ContentType;
import org.apache.coyote.FilePath;
import org.apache.coyote.Processor;
import org.apache.coyote.QueryStringHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            BufferedReader inputBufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String uri = inputBufferedReader.readLine().split(" ")[1];
            if (uri.contains("?")) {
                Map<String, String> queryParams = QueryStringHandler.queryParams(uri);
                Optional<User> user = InMemoryUserRepository.findByAccount(queryParams.get("account"));
                user.ifPresent(value -> log.info(value.toString()));
            }
            FilePath filePath = FilePath.from(uri);

            var responseBody = getResponseBody(filePath.getValue());

            outputStream.write(getResponse(ContentType.find(filePath.getValue()), responseBody).getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponseBody(String uriPath) throws URISyntaxException, IOException {
        if (uriPath.equals("/")) {
            return "Hello world!";
        }
        String fileName = "static" + uriPath;
        final URL resource = getClass().getClassLoader().getResource(fileName);
        final File file = Paths.get(resource.toURI()).toFile();
        return new String(Files.readAllBytes(file.toPath()));
    }


    private String getResponse(ContentType contentType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType.getType() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
