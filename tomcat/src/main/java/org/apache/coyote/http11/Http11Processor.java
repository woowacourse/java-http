package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Map;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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

            var responseBody = "Hello world!";
            Http11Request request = Http11Request.from(inputStream);

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (request.isStaticRequest()) {
                InputStream stream = loader.getResourceAsStream("static/" + request.getUri());
                assert stream != null;
                responseBody = new String(stream.readAllBytes());
                stream.close();
            }
            if (!request.isStaticRequest()) {
                if (request.getUri().startsWith("/login")) {
                    Map<String, String> queryParameters = request.getQueryParameters();
                    User user = InMemoryUserRepository.findByAccount(queryParameters.get("account"))
                            .orElseThrow(() -> new RuntimeException("400"));// 400 BadRequest
                    if (!user.checkPassword(queryParameters.get("password"))) {
                        throw new RuntimeException("401"); // 401 UnAuthorized
                    }
                    // login ok
                    System.out.println(user);
                }
            }

            int index = request.getUri().lastIndexOf(".");
            String fileExtension = request.getUri().substring(index + 1);
            if (fileExtension.equals("js")) {
                fileExtension = "javascript";
            }

            Http11Response response = new Http11Response(responseBody, fileExtension);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
