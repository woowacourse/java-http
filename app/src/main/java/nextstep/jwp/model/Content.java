package nextstep.jwp.model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Content {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private final String value;

    public Content(String value) {
        this.value = value;
    }

    public static Content readFile(File file) throws IOException {
        return new Content(String.join(NEW_LINE, Files.readAllLines(file.toPath())));
    }

    public String getValue() {
        return value;
    }

    public int getLength() {
        return value.getBytes(StandardCharsets.UTF_8).length;
    }
}
