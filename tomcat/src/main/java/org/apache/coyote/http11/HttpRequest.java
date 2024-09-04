package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URI;
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

    private final String value;

    public HttpRequest(String value) {
        this.value = value;
    }

    public String getResponse() {
        String requestUrl = "static" + value.split(" ")[1];
        URL resource = getClass().getClassLoader().getResource(requestUrl);
        return createResponse(resource);
    }

    private String createResponse(URL resource) {
        try {
            String responseBody = readFile(resource.toURI());
            return getResponse(responseBody);
        } catch (NullPointerException | IOException | URISyntaxException e) {
            log.error(e.getMessage());
            return getResponse(BASIC_RESPONSE_BODY);
        }
    }

    private String readFile(URI resource) throws IOException {
        Path path = Path.of(resource);
        List<String> fileLines = Files.readAllLines(path);

        StringJoiner joiner = new StringJoiner(DELIMITER);
        for (String fileLine : fileLines) {
            joiner.add(fileLine);
        }
        joiner.add("");
        return joiner.toString();
    }

    private String getResponse(String responseBody) {
        return String.join(DELIMITER,
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
