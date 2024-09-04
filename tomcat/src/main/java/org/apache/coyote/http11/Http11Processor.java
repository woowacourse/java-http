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
    private static final String GET_METHOD = "GET";
    private static final String DEFAULT_ROUTE = "/";
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
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
            final var response = bindResponse(requestFirstLine);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String bindResponse(String requestFirstLine) throws IOException {
        String httpMethod = requestFirstLine.split(" ")[0];
        String route = requestFirstLine.split(" ")[1];

        if (DEFAULT_ROUTE.equals(route) && GET_METHOD.equals(httpMethod)) {
            return buildSuccessfulResponse(DEFAULT_RESPONSE_BODY);
        }

        if (HOME_PAGE_ROUTE.equals(route) && GET_METHOD.equals(httpMethod)) {
            URL resource = getClass().getClassLoader().getResource(HOME_PAGE_RESPONSE_BODY);
            if (resource == null) {
                throw new InternalError("기본으로 보여줄 파일을 찾을 수 없습니다.");
            }
            String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            return buildSuccessfulResponse(body);
        }

        return buildFailedResponse(404, "NotFound",
                "There is no static resource matched with rout and method");
    }

    private String buildSuccessfulResponse(String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String buildFailedResponse(int statusCode, String statusMessage, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " " + statusMessage,
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
