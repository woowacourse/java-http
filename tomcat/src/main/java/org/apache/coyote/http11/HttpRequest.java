package org.apache.coyote.http11;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.StringJoiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
    private static final String DELIMITER = "\r\n";
    private static final String BASIC_RESPONSE_BODY = "Hello world!";

    private final HttpStartLine httpStartLine;
    private final HttpHeaders httpHeaders;

    public HttpRequest(HttpStartLine httpStartLine, HttpHeaders httpHeaders) {
        this.httpStartLine = httpStartLine;
        this.httpHeaders = httpHeaders;
    }

    public String createResponse() {
        String requestUrl = httpStartLine.getRequestTarget();
        URL resource = getClass().getClassLoader().getResource(requestUrl);
        return createResponse(resource);
    }

    private String createResponse(URL resource) {
        try {
            Path path = Path.of(resource.toURI());
            if (Files.exists(path)) {
                String responseBody = readFile(path);
                String contentType = "text/" + httpStartLine.getTargetExtension();
                return getResponse(responseBody, contentType);
            }
            throw new FileNotFoundException(resource.toString());
        } catch (NullPointerException | IOException | URISyntaxException e) {
            log.error(e.getMessage());
            return getResponse(BASIC_RESPONSE_BODY, "text/html");
        }
    }

    private String readFile(Path path) throws IOException {
        List<String> fileLines = Files.readAllLines(path);

        StringJoiner joiner = new StringJoiner(DELIMITER);
        for (String fileLine : fileLines) {
            joiner.add(fileLine);
        }
        joiner.add("");
        return joiner.toString();
    }

    private String getResponse(String responseBody, String contentType) {
        return String.join(DELIMITER,
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
