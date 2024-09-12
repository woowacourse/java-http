package org.apache.coyote.response;

import java.nio.file.Files;
import java.nio.file.Path;

public class ResponseBody {

    public static final String STATIC_PATH = "/static";

    private final String content;

    public ResponseBody() {
        this.content = "";
    }

    public void setBody(String resource) {
        StringBuilder rawBody = new StringBuilder();
        Path path = Path.of(getClass().getResource(STATIC_PATH + resource).getPath());

        Files.readAllLines(path)
                .forEach(line -> rawBody.append(line).append("\r\n"));
    }

    public int getLength() {
        return content.length();
    }

    public String getContent() {
        return content;
    }
}
