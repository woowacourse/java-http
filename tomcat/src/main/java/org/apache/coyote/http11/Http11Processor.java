package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
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
                    new String(Files.readAllBytes(new File(ClassLoader.getSystemResource(STATIC_FILE_PREFIX + httpRequest.getPath() + ".html").getFile()).toPath()))
                );
            } else if (httpRequest.getUri().contains(".")) {
                int extensionIndex = httpRequest.getUri().lastIndexOf(".");
                String extension = httpRequest.getUri().substring(extensionIndex + 1);

                // TODO: getSystemResource(...).getFile()은 리소스 미존재 시 NPE, 경로 인코딩/패키징(JAR 내부) 이슈가 있습니다. 미존재(404)와 I/O 예외를 구분해 응답하세요.
                if (extension.equals("html") || extension.equals("css") || extension.equals("js")) {
                    String staticFile = new String(Files.readAllBytes(new File(ClassLoader.getSystemResource(STATIC_FILE_PREFIX + httpRequest.getUri()).getFile()).toPath()));
                    response = getHttpResponse(httpRequest.getUri().substring(httpRequest.getUri().indexOf(".") + 1), staticFile);
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
