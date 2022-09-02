package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
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

            final String requestValue = readHttpRequest(inputStream);
            if (requestValue == null) {
                return;
            }

            final String requestPath = requestValue.split(" ")[1];

            String contentType = "text/html";

            if (requestPath.contains("/css")) {
                contentType = "text/css";
            }

            final String responseBody = makeResponseBody(requestPath);
            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readHttpRequest(final InputStream inputStream) throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        final String requestValue = br.readLine();
        return requestValue;
    }

    private String makeResponseBody(final String requestPath) throws IOException, URISyntaxException {
        if ("/".equals(requestPath)) {
            return "Hello world!";
        }

        if (requestPath.equals("/login")) {
            final URI uri = getClass().getClassLoader().getResource("static" + requestPath + ".html").toURI();
            return new String(Files.readAllBytes(Paths.get(uri)));
        }

        if (requestPath.contains("/login")) {
            final Map<Integer, String> params = QueryStringParser.parsing(requestPath);
            checkLogin(params.get(0), params.get(1));
            return "success";
        }

        final URI uri = getClass().getClassLoader().getResource("static" + requestPath).toURI();
        return new String(Files.readAllBytes(Paths.get(uri)));
    }

    private void checkLogin(final String inputAccountValue, final String inputPasswordValue) {
        final User user = InMemoryUserRepository.findByAccount(inputAccountValue)
                .orElseThrow();

        final boolean isSuccessLogin = user.checkPassword(inputPasswordValue);
        if (isSuccessLogin) {
            log.debug("로그인 성공 = {}", user);
        }
    }
}
