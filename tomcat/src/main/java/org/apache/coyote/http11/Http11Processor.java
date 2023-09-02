package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.ContentType;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.join;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String CONTENT_TYPE_PREFIX = "Accept: ";
    private static final String DEFAULT_RESPONSE_BODY_MESSAGE = "Hello world!";

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
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        ) {
            List<String> request = readRequest(bufferedReader);
            ContentType contentType = readContentType(request);

            var requestURI = request.get(0).split(" ")[1];

            if (requestURI.contains("?")) {
                final int index = requestURI.indexOf("?");
                requestURI = requestURI.substring(0, index) + ".html";
                QueryStrings queryStrings = QueryStrings.from(requestURI);

                String account = queryStrings.getValues().get("account");
                User user = InMemoryUserRepository.findByAccount(account)
                        .orElseThrow(IllegalArgumentException::new);
                log.info(user.toString());
            }

            String response = makeResponse(requestURI, contentType);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> readRequest(BufferedReader bufferedReader) throws IOException {
        List<String> request = new ArrayList<>();
        String line;
        while (!(line = bufferedReader.readLine()).isBlank()) {
            request.add(line);
        }
        return request;
    }

    private ContentType readContentType(List<String> request) {
        String extension = request.stream()
                .filter(it -> it.startsWith(CONTENT_TYPE_PREFIX))
                .findFirst()
                .orElse("html");
        return ContentType.from(extension);
    }

    private String makeResponse(String requestURI, ContentType contentType) throws IOException {
        String responseBody = makeResponseBody(requestURI);
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType.getValue() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String makeResponseBody(String requestURI) throws IOException {
        System.out.println(requestURI);
        if ("/".equals(requestURI)) {
            return DEFAULT_RESPONSE_BODY_MESSAGE;
        }
        Path path = new File(Objects.requireNonNull(
                getClass().getClassLoader().getResource("static" + requestURI)).getFile()
        ).toPath();
        return new String(Files.readAllBytes(path));
    }

}
