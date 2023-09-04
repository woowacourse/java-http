package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import nextstep.jwp.dto.ResponseDto;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.service.UserService;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_URL = "/default.html";
    private static final String EMPTY_URL = "/";
    private static final String PATH = "static";
    private static final String END_OF_LINE = "";

    private boolean isActive = false;
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
        try (
                final InputStream inputStream = connection.getInputStream();
                final OutputStream outputStream = connection.getOutputStream();
                final BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8)
                )
        ) {
            HttpRequestHeader header = readHttpRequestHeader(reader);

            String response = makeResponse(header, reader);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequestHeader readHttpRequestHeader(BufferedReader reader) throws IOException {
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null & !Objects.equals(END_OF_LINE, line)) {
            builder.append(line).append(System.lineSeparator());
        }

        return HttpRequestHeader.from(builder.toString());
    }

    private String makeResponse(HttpRequestHeader header, BufferedReader reader) throws IOException {
        String url = header.url();

        if (Objects.equals(header.httpMethod(), "POST")) {
            String bodyContent = readBodyContent(header, reader);
            HttpRequestBody requestBody = HttpRequestBody.from(bodyContent);

            if (url.contains("/login")) {
                ResponseDto responseDto = UserService.checkUserCredentials(
                        requestBody.get("account"),
                        requestBody.get("password")
                );

                String location = responseDto.location();
                String code = responseDto.code();
                FileManager fileManager = FileManager.from(location);
                String type = fileManager.mimeType();
                String responseBody = fileManager.fileContent();

                return String.join(
                        System.lineSeparator(),
                        "HTTP/1.1 " + code,
                        "Location:" + location,
                        "Content-Type: " + type + ";charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody
                );
            }

            if (url.contains("/register")) {
                ResponseDto responseDto = UserService.save(
                        requestBody.get("account"),
                        requestBody.get("password"),
                        requestBody.get("email")
                );

                String location = responseDto.location();
                String code = responseDto.code();
                FileManager fileManager = FileManager.from(location);
                String type = fileManager.mimeType();
                String responseBody = fileManager.fileContent();

                return String.join(
                        System.lineSeparator(),
                        "HTTP/1.1 " + code,
                        "Location:" + location,
                        "Content-Type: " + type + ";charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody
                );
            }
        }

        if (Objects.equals(url, "/")) {
            String responseBody = "Hello world!";
            return String.join(
                    System.lineSeparator(),
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody
            );
        }

        FileManager fileManager = FileManager.from(url);
        String type = fileManager.mimeType();
        String responseBody = fileManager.fileContent();

        return String.join(
                System.lineSeparator(),
                "HTTP/1.1 " + "200 OK ",
                "Content-Type: " + type + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );
    }

    private String readBodyContent(HttpRequestHeader header, BufferedReader reader) throws IOException {
        int contentLength = Integer.parseInt(header.contentLength());
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }
}
