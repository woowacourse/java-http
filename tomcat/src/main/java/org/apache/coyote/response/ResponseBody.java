package org.apache.coyote.response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResponseBody {

    public static final String STATIC_PATH = "/static";

    private String content;

    public ResponseBody() {
        this.content = "";
    }

    public void setBody(String resource) {
        StringBuilder rawBody = new StringBuilder();
        try {
            Path path = Path.of(getClass().getResource(STATIC_PATH + resource).getPath());
            Files.readAllLines(path)
                    .forEach(line -> rawBody.append(line).append("\r\n"));
        } catch (NullPointerException | IOException e) {
            throw new IllegalArgumentException("Not found resource");
        }
        content = rawBody.toString();
    }

    public int getLength() {
        return content.length();
    }

    public String getContent() {
        return content;
    }
}
