package org.apache.coyote.http11;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_URL = "/default.html";
    private static final String EMPTY_URL = "/";
    private static final String PATH = "static";
    private static final String END_OF_LINE = "";

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
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()
        ) {
            // read input stream
            String request = readAsUTF8(inputStream);

            // parse headers
            Map<String, String> headers = parseHeaders(request);
            String url = headers.get("URL");

            // file type
            Path path = mapToResourcePath(url);
            String fileName = path.getFileName().toString();
            String fileExtension = extractFileExtension(fileName);
            String type = mapMimeType(fileExtension);

            // resource
            String resource = new String(Files.readAllBytes(path));

            String response = makeResponse(resource, type);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String mapMimeType(String fileExtension) {
        String type = "";
        if (Objects.equals(fileExtension, "html")) {
            type = "text/html";
        }
        if (Objects.equals(fileExtension, "css")) {
            type = "text/css";
        }
        if (Objects.equals(fileExtension, "svg")) {
            type = "image/svg+xml";
        }
        if (Objects.equals(fileExtension, "js")) {
            type = "text/javascript";
        }
        return type;
    }

    private String extractFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");

        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }

    private String readAsUTF8(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null & !Objects.equals(END_OF_LINE, line)) {
            builder.append(line).append(System.lineSeparator());
        }

        return builder.toString();
    }

    private Map<String, String> parseHeaders(String request) {
        Map<String, String> headers = new HashMap<>();
        List<String> lines = Arrays.stream(request.split(System.lineSeparator())).collect(toList());

        List<String> httpInformation = Arrays.stream(lines.get(0).split(" ")).collect(toList());
        headers.put("HTTP Method", httpInformation.get(0));
        headers.put("URL", httpInformation.get(1));
        headers.put("HTTP version", httpInformation.get(2));

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            int standardIndex = line.indexOf(":");
            String header = line.substring(0, standardIndex).strip();
            String content = line.substring(standardIndex + 1).strip();
            headers.put(header, content);
        }
        return headers;
    }

    private Path mapToResourcePath(String url) throws IOException {
        if (Objects.equals(url, EMPTY_URL)) {
            url = DEFAULT_URL;
        }

        URL locate = getClass().getClassLoader().getResource(PATH + url);
        return new File(Objects.requireNonNull(locate).getFile()).toPath();
    }

    private String makeResponse(String responseBody, String type) {
        return String.join(
                System.lineSeparator(),
                "HTTP/1.1 200 OK ",
                "Content-Type: " + type + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody
        );
    }
}
