package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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

            Map<String, String> requestValues = readRequest(inputStream);
            ClassLoader classLoader = getClass().getClassLoader();
            URL url = classLoader.getResource(requestValues.get("Uri"));

            var responseBody = "Hello world!";
            if(url != null){
                Path resourcePath = Path.of(url.toURI());
                if(Files.isRegularFile(resourcePath)){
                    log.info(resourcePath.toString());
                    List<String> resourceValues = Files.readAllLines(resourcePath);
                    responseBody = resourceValues.stream()
                            .collect(Collectors.joining("\n")) + "\n";
                }
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> readRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        Map<String, String> values = new HashMap<>();

        String[] requestLine = bufferedReader.readLine().split(" ");
        values.put("Method", requestLine[0]);
        values.put("Uri", "static" + requestLine[1]);
        values.put("Version", requestLine[2]);

        String[] header = bufferedReader.readLine().split(": ");
        while (header.length == 2){
            values.put(header[0], header[1]);
            header = bufferedReader.readLine().split(": ");
        }

        return values;
    }
}
