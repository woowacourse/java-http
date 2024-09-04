package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String HOME_PAGE_METHOD = "GET";
    private static final String HOME_PAGE_ROUTE = "/index.html";
    private static final String HOME_PAGE_RESPONSE_BODY = "static/index.html";

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

            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String requestFirstLine = bufferedReader.readLine();

            final var responseBody = buildResponseBody(requestFirstLine);
            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String buildResponseBody(String requestFirstLine) throws IOException {
        String httpMethod = requestFirstLine.split(" ")[0];
        String route = requestFirstLine.split(" ")[1];
        if (!HOME_PAGE_METHOD.equals(httpMethod) || !HOME_PAGE_ROUTE.equals(route)) {
            return DEFAULT_RESPONSE_BODY;
        }

        URL resource = getClass().getClassLoader().getResource(HOME_PAGE_RESPONSE_BODY);
        if (resource == null) {
            throw new InternalError("기본으로 보여줄 파일을 찾을 수 없습니다.");
        }

        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
