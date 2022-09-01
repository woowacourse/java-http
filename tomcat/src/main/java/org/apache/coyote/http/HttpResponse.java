package org.apache.coyote.http;

import static org.apache.coyote.PageMapper.isFileRequest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.PageMapper;

public class HttpResponse {

    private String body;

    public HttpResponse(final String url) {
        final String responseBody = makeResponseBody(url);
        this.body = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String makeResponseBody(String url) {
        if (isFileRequest(url)) {
            final Path filePath;
            try {
                filePath = new PageMapper(url).getFilePath();
                return readFile(filePath);
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
        }
        return "Hello world!";
    }

    private String readFile(Path path) throws IOException {
        return new String(Files.readAllBytes(path));
    }

    public String getBody() {
        return body;
    }
}
