package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.dto.Http11Request;
import org.apache.coyote.http11.headers.Extension;
import org.apache.coyote.http11.utils.StringParser;
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
             final var outputStream = connection.getOutputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            String uri = getFileName(bufferedReader);
            String contentType = getContentType(uri);
            String responseBody = getResponseBody(uri);

            final var response = createResponse(contentType, responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getContentType(String file) {
        Extension fileExtension = Extension.create(file);
        return fileExtension.getContentType();
    }

    private String getFileName(final BufferedReader bufferedReader) throws IOException {
        return bufferedReader.readLine()
                .split(" ")[1]
                .substring(1);
    }

    private String getResponseBody(final String uri) throws IOException, URISyntaxException {
        String path = uri;
        if (uri.isEmpty()) {
            return "Hello world!";
        }
        if (uri.startsWith("login")) {
            Http11Request http11Request = StringParser.loginQuery(uri);

            User user = InMemoryUserRepository.findByAccount(http11Request.getAccount())
                    .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호를 잘못 입력하였습니다."));
            log.info("user : {}", user);
            path = http11Request.getPath();
        }
        URL resource = this.getClass()
                .getClassLoader()
                .getResource("static/" + path);

        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private String createResponse(String contentType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: "+ contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
