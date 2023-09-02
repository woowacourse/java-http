package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final Pattern ACCEPT_TYPE = Pattern.compile("Accept: (.+?)\r\n");
    private static final Pattern REQUEST_URI = Pattern.compile("GET (.+?) ");

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
             final var outputStream = connection.getOutputStream();
             final var inputStreamReader = new InputStreamReader(inputStream);
             final var bufferedReader = new BufferedReader(inputStreamReader)) {

            final String requestHeader = getRequestHeader(bufferedReader);
            final String acceptType = getAcceptType(requestHeader);
            final String requestUri = getRequestUri(requestHeader);
            final String fileContent = getFileContent(requestUri);
            final String response = getResponse(acceptType, fileContent);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(final String acceptType, final String fileContent) {
        String contentType = "text/html";
        if (acceptType.startsWith("text/css")) {
            contentType = "text/css";
        }
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + fileContent.getBytes().length + " ",
                "",
                fileContent);
    }

    private String getRequestHeader(final BufferedReader bufferedReader) throws IOException {
        final StringBuilder requestHeader = new StringBuilder();
        while (true) {
            final String line = bufferedReader.readLine();
            if (line == null || "".equals(line)) {
                break;
            }
            requestHeader.append(line).append("\r\n");
        }
        return requestHeader.toString();
    }

    private String getAcceptType(final String requestHeader) {
        final Matcher acceptTypeMatcher = ACCEPT_TYPE.matcher(requestHeader);
        String acceptType = "";
        if (acceptTypeMatcher.find()) {
            acceptType = acceptTypeMatcher.group(1);
            log.info("acceptType = {}", acceptType);
        }
        return acceptType;
    }

    private String getRequestUri(final String requestHeader) {
        final Matcher requestUriMatcher = REQUEST_URI.matcher(requestHeader);
        String requestUri = "";
        if (requestUriMatcher.find()) {
            requestUri = requestUriMatcher.group(1);
            log.info("requestUri = {}", requestUri);
        }
        return requestUri;
    }

    private String getFileContent(String fileName) throws IOException {
        try {
            if (fileName.equals("/")) {
                fileName = "/index.html";
            }
            final URL resource = getClass().getClassLoader().getResource("static" + fileName);
            final Path path = Paths.get(resource.getPath());
            log.info("filePath = {}", path);
            return Files.readString(path);
        } catch (final NullPointerException e) {
            return "";
        }
    }
}
