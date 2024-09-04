package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String RESOURCE_PATH = "static/";

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
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()
        ) {
            List<String> request = getRequest(inputStream);
            String requestUrl = request.getFirst()
                    .split(" ")[1];

            outputStream.write(getResponse(requestUrl).getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> getRequest(InputStream inputStream) throws IOException {
        List<String> request = new ArrayList<>();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        while (bufferedReader.ready()) {
            request.add(bufferedReader.readLine());
        }

        return request;
    }

    private String getResponse(String requestUrl) throws IOException {
        String responseBody = getResponseBody(requestUrl);

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + ContentType.findContentType(requestUrl) + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String getResponseBody(String requestUrl) throws IOException {
        if (requestUrl.equals("/")) {
            return "Hello world!";
        }

        return Files.readString(Path.of(getClass().getClassLoader()
                .getResource(RESOURCE_PATH + requestUrl)
                .getPath()));
    }
}
