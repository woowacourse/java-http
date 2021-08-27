package nextstep.jwp.model;

import java.nio.charset.StandardCharsets;

public class Content {

    private final String value;

    public Content(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public int getLength() {
        return value.getBytes(StandardCharsets.UTF_8).length;
    }
}
