package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String NOT_FOUND_PAGE = "static/404.html";
    private static final String WELCOM_PATH = "/";

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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            // HTTP 요청 읽기
            final HttpRequest httpRequest = toHttpRequest(inputStream);

            // 화면 렌더링
            if (WELCOM_PATH.equals(httpRequest.getPath())) {
                renderWelcomePage(outputStream);
            } else {
                render(outputStream, httpRequest);
            }

        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static HttpRequest toHttpRequest(final InputStream inputStream) throws IOException {
        final List<String> rawHttpRequest = readHttpRequest(inputStream);
        return HttpRequest.from(rawHttpRequest);
    }

    private static List<String> readHttpRequest(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final List<String> rawHttpRequest = new ArrayList<>();

        String line = bufferedReader.readLine();
        rawHttpRequest.add(line);
        while (!"".equals(line)) {
            if (line == null) {
                break;
            }
            line = bufferedReader.readLine();
            rawHttpRequest.add(line);
        }

        bufferedReader.close();
        return rawHttpRequest;
    }

    private static void renderWelcomePage(final OutputStream outputStream) throws IOException {
        final String responseBody = "Hello world!";
        final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void render(final OutputStream outputStream, final HttpRequest httpRequest) throws IOException {
        // static 파일
        final String requestUri = httpRequest.getPath();
        String viewName = "static" + requestUri;
        if ("/login".equals(requestUri)) {
            viewName = "static/login.html";

            if (httpRequest.haveParam()) {
                final String account = httpRequest.getParam("account");
                final String password = httpRequest.getParam("password");
                final User user = InMemoryUserRepository.findByAccount(account)
                        .orElseThrow(NoSuchElementException::new);
                if (user.checkPassword(password)) {
                    final String outputMessage = user.toString();
                    log.info(outputMessage);
                }
            }
        }

        URL resource = getClass().getClassLoader().getResource(viewName);
        if (resource == null || viewName.endsWith(WELCOM_PATH)) {
            resource = getClass().getClassLoader().getResource(NOT_FOUND_PAGE);
        }

        final Path path = new File(resource.getFile()).toPath();
        final byte[] indexHtml = Files.readAllBytes(path);

        final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + Files.probeContentType(path) + ";charset=utf-8 ",
                "Content-Length: " + indexHtml.length + " ",
                "",
                new String(indexHtml));

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
