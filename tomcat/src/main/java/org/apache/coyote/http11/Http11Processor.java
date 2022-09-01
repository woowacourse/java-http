package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.jwp.exception.UncheckedServletException;
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

            final List<String> httpRequestHeaderInformation = readInputStream(inputStream);
            if (httpRequestHeaderInformation == null) {
                return;
            }

            // 저장한 header 정보에서 첫 째줄 가져오고 uri를 파싱한다.
            List<String> httpRequestMethodInformation = Arrays.stream(httpRequestHeaderInformation.get(0)
                    .split(" "))
                    .collect(Collectors.toList());
            String requestUri = httpRequestMethodInformation.get(1);

            final String response = getResponse(requestUri);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(String requestUri) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static" + requestUri);
        assert resource != null;
        final Path path = new File(resource.getPath()).toPath();
        byte[] bytes = Files.readAllBytes(path);
        final String responseBody = new String(bytes);

        final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
        return response;
    }

    private List<String> readInputStream(java.io.InputStream inputStream) throws IOException {
        final List<String> httpRequestHeaderInformation = new ArrayList<>();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        while (!"".equals(line)) {
            httpRequestHeaderInformation.add(line);
            line = reader.readLine();
            if (line == null) {
                return null;
            }
        }
        return httpRequestHeaderInformation;
    }
}
