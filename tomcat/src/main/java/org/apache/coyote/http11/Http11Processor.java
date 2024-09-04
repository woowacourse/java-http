package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.http.HttpHeaders;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String HEADER_ACCEPT = "Accept";

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
            BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));

            String[] httpRequest = input.readLine().split(" ");

            Map<String, String> httpHeaders = getHttpHeaders(input);
            String accept = httpHeaders.get(HEADER_ACCEPT);
            if(accept.contains("text/css")) {
                final String responseBody = getFileValue(httpRequest);
                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/css;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
                outputStream.write(response.getBytes());
                outputStream.flush();
            } else {
                final String responseBody = getFileValue(httpRequest);
                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);

                outputStream.write(response.getBytes());
                outputStream.flush();
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> getHttpHeaders(BufferedReader input) throws IOException {
        Map<String, String> httpHeader = new HashMap<>();

        String value = input.readLine();
        while(!value.equals("")) {
            String[] header = value.split(": ");
            httpHeader.put(header[0], header[1]);

            value = input.readLine();
        }

        return httpHeader;
    }

    private String getFileValue(String[] httpRequest) throws IOException {
        String resourcePath = "static" + httpRequest[1];
        URL resource = getClass().getClassLoader().getResource(resourcePath);
        final Path path = new File(resource.getPath()).toPath();

        final String responseBody = Files.readString(path);
        return responseBody;
    }

}
