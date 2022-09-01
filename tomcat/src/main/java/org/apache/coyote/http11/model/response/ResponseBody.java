package org.apache.coyote.http11.model.response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.apache.coyote.http11.model.ContentFormat;

public class ResponseBody {

    private final ContentFormat contentFormat;
    private final String body;

    public ResponseBody(final String url) throws IOException {
        String responseBody;

        if (url.endsWith("/")) {
            responseBody = "Hello world!";
        } else {
            Path path = Path.of(Objects.requireNonNull(this.getClass().getResource("/static" + url)).getPath());
            responseBody = Files.readString(path);
        }
        this.body = responseBody;
        this.contentFormat = ContentFormat.findByExtension(url);
    }

    public int getContentLength() {
        return body.getBytes().length;
    }

    public String getContentType() {
        return contentFormat.getValue();
    }

    public String getBody() {
        return body;
    }
}
