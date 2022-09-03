package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResponseProcessor {

    private static final String STATIC_PATH = "static/";

    private final Path path;
    private final QueryParameters queryParameters;
    private final String responseBody;

    public ResponseProcessor(StartLine startLine) throws URISyntaxException, IOException {
        String uri = startLine.getUri();
        this.path = Path.of(uri);
        this.queryParameters = QueryParameters.of(uri);
        this.responseBody = processResponseBody();
    }

    public String getResponse() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/" + path.extractContentType() + ";charset=utf-8 ",
                "Content-Length: " + extractContentLength() + " ",
                "",
                responseBody);
    }

    private String processResponseBody() throws URISyntaxException, IOException {
        if (path.isFileRequest()) {
            String fileName = path.getFileName();
            return readFile(fileName);
        }
        return processResponseBody(Controller.processRequest(path.value(), queryParameters));
    }

    private String processResponseBody(String response) throws URISyntaxException, IOException {
        if (doesNeedViewFile(response)) {
            return readFile(response);
        }
        return response;
    }

    private boolean doesNeedViewFile(String response) {
        return response.endsWith(".html");
    }

    private String readFile(String fileName) throws URISyntaxException, IOException {
        final URI fileUri = getClass().getClassLoader().getResource(STATIC_PATH + fileName).toURI();
        byte[] lines = Files.readAllBytes(Paths.get(fileUri));
        return new String(lines);
    }

    private int extractContentLength() {
        return responseBody.getBytes().length;
    }
}
