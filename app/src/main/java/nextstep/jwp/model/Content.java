package nextstep.jwp.model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Content {

    private final String value;

    private Content(String value) {
        this.value = value;
    }

    public static Content readFile(File file) throws IOException {
        String contentValue = new String(Files.readAllBytes(file.toPath()));
        return new Content(contentValue);
    }

    public String getValue() {
        return value;
    }

    public int getLength() {
        return value.getBytes(StandardCharsets.UTF_8).length;
    }
}
