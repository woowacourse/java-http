package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final int PATH_INDEX = 1;

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
        try (InputStream inputStream = connection.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
             OutputStream outputStream = connection.getOutputStream()) {

            URI uri = new URI(readRequestPath(bufferedReader));
            final String response = executeRequestAndGetResponse(uri);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readRequestPath(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        return line.split(" ")[PATH_INDEX];
    }

    private String executeRequestAndGetResponse(URI uri) throws IOException {
        String requestPath = uri.getPath();

        if ("/".equals(requestPath)) {
            return makeResponse(ContentType.HTML.getContentType(), "Hello world!");
        }

        if ("/login".equals(requestPath)) {
            doLoginRequest(uri);
            return makeResponse(requestPath.concat("." + ContentType.HTML.getExtension()));
        }

        return makeResponse(requestPath);
    }

    private String makeResponse(String contentType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String makeResponse(String file) throws IOException {
        final String responseBody = readFile("static" + file);
        return makeResponse(ContentType.findContentType(file), responseBody);
    }

    private String readFile(String fileName) throws IOException {
        URL resource = getClass().getClassLoader().getResource(fileName);
        final Path path = Path.of(Objects.requireNonNull(resource).getPath());

        return Files.readString(path);
    }

    private void doLoginRequest(URI uri) {
        QueryMapper queryMapper = new QueryMapper(uri);
        Map<String, String> parameters = queryMapper.getParameters();

        User user = InMemoryUserRepository.findByAccount(parameters.get("account"))
                .orElseThrow(NoSuchElementException::new);

        if (user.checkPassword(parameters.get("password"))) {
            log.info("user : " + user);
        }
    }
}
