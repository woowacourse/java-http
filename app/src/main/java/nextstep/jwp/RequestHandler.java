package nextstep.jwp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RequestHandler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);
    private static final String NEW_LINE = System.getProperty("line.separator");

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        LOG.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            final String responseBody = getResponseBody(inputStream);
            final String response = getResponse(responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | URISyntaxException exception) {
            LOG.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private String getResponseBody(InputStream inputStream) throws IOException, URISyntaxException {
        final List<String> header = getHeader(inputStream);
        final Path filePath = getFilePathFromHeader(header);
        final List<String> fileLines = Files.readAllLines(filePath);

        return String.join(NEW_LINE, fileLines);
    }

    private List<String> getHeader(InputStream inputStream) throws IOException {
        final StringBuilder stringBuilder = getStringBuilderOfInputStreamContent(inputStream);
        final String[] splitStringBuilderContent = stringBuilder.toString().split(NEW_LINE);

        return Arrays.stream(splitStringBuilderContent)
                .collect(Collectors.toList());
    }

    private StringBuilder getStringBuilderOfInputStreamContent(InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = getBufferedReader(inputStream);
        final StringBuilder stringBuilder = new StringBuilder();

        String line = null;
        while (!"".equals(line)) {
            line = bufferedReader.readLine();
            validateLineIsNotNull(line);
            stringBuilder.append(line);
            stringBuilder.append(NEW_LINE);
        }

        return stringBuilder;
    }

    private BufferedReader getBufferedReader(InputStream inputStream) {
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        return new BufferedReader(inputStreamReader);
    }

    private void validateLineIsNotNull(String line) throws IOException {
        if (line == null) {
            throw new IOException("BufferedReader에서 readLine()으로 읽은 line의 값이 null입니다.");
        }
    }

    private Path getFilePathFromHeader(List<String> header) throws IOException, URISyntaxException {
        final String fileName = getFileNameFromHeader(header);
        final URL url = getClass().getClassLoader().getResource("static/" + fileName);
        if (url == null) {
            throw new IOException("fileName으로 찾은 url의 값이 null 입니다.");
        }
        return Paths.get(url.toURI());
    }

    private String getFileNameFromHeader(List<String> header) {
        final String headerFirstLine = header.get(0);
        final String requestUri = headerFirstLine.split(" ")[1];
        return requestUri.substring(1);
    }

    private String getResponse(String responseBody) {
        return String.join(NEW_LINE,
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            LOG.error("Exception closing socket", exception);
        }
    }
}
