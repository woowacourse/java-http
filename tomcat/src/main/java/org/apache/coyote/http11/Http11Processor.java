package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.MediaType;
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
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = readRequest(inputStream);
            HttpResponse response = createResponse(httpRequest);
            writeResponse(outputStream, response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();

        String line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            sb.append(line).append("\r\n");
            line = reader.readLine();
        }

        return new HttpRequest(sb.toString());
    }

    private HttpResponse createResponse(HttpRequest request) throws IOException {
        // 정적 리소스 중 어떤 타입을 요청하는지 확장자를 통해 확인
        String pathStr = request.getPath();
        String requestedExtension = pathStr.substring(pathStr.lastIndexOf(".") + 1);
        MediaType mediaType = MediaType.ofExtension(requestedExtension);
        log.info("Requested MediaType: {}", mediaType);

        // 정적 리소스 읽어옴
        URL resource = getClass().getClassLoader().getResource("static" + request.getPath());
        Path path = Optional.ofNullable(resource)
                .map(URL::getPath)
                .map(Path::of)
                .orElseThrow(() -> new RuntimeException(request.getPath() + " not found"));

        List<String> strings = Files.readAllLines(path);
        String responseBody = String.join("\r\n", strings);
        HttpResponse response = new HttpResponse(1.1, 200, "OK")
                .addHeader("Content-Type", mediaType.getValue())
                .addHeader("Content-Length", String.valueOf(responseBody.getBytes().length))
                .setBody(responseBody);

        return response;
    }

    private void writeResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        outputStream.write(response.getAsBytes());
        outputStream.flush();
    }
}
