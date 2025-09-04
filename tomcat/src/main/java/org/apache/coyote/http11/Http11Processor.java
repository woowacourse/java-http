package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String startLine = bufferedReader.readLine();
            String rawPath = startLine.split(" ")[1];
            String responseBody;

            if (rawPath.equals("/")) {
                responseBody = "Hello world!";
                writeResponse(responseBody, "html", outputStream);
            }

            if (rawPath.contains("?")) {
                int index = rawPath.indexOf("?");
                Map<String, String> queryParams = parseQueryParams(rawPath, index);
                Optional<User> user = InMemoryUserRepository.findByAccount(queryParams.get("account"));
                System.out.println("user = " + user);

                rawPath = rawPath.substring(0, index) + ".html";
            }

            String contentType = getContentType(rawPath);
            Path filePath = getFilePath(rawPath);
            String header = getHeader(bufferedReader);

            responseBody = new String(Files.readAllBytes(filePath));
            writeResponse(responseBody, contentType, outputStream);

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> parseQueryParams(String path, int index) {
        String queryString = path.substring(index + 1);
        String[] queries = queryString.split("&");

        Map<String, String> queryParams = new HashMap<>();
        for (String query : queries) {
            String[] paramPair = query.split("=");
            queryParams.put(paramPair[0], paramPair[1]);
        }
        return queryParams;
    }

    private String getContentType(String path) {
        String[] split = path.split("\\.");
        return split[split.length - 1];
    }

    private Path getFilePath(String path) {
        URL resource = getClass().getClassLoader().getResource("static" + path);
        return new File(resource.getFile()).toPath();
    }

    private String getHeader(BufferedReader bufferedReader) throws IOException {
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            sb.append(line).append("\r\n");
        }
        return sb.toString();
    }

    private void writeResponse(String responseBody, String contentType, OutputStream outputStream) throws IOException {
        String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + "text/" + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
