package nextstep.jwp.http.header.element;

import static nextstep.jwp.http.Protocol.LINE_SEPARATOR;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;

public class Body {

    private static final Body EMPTY_BODY = new Body(null);

    private final String body;

    public Body(String body) {
        this.body = body;
    }

    public static Body empty() {
        return EMPTY_BODY;
    }

    public static Body fromFile(File file) {
        try {
            String content = String.join(LINE_SEPARATOR.value(), Files.readAllLines(file.toPath()));

            return new Body(content);
        } catch (IOException e) {
            throw new IllegalArgumentException("파일을 읽는데 실패했습니다.", e);
        }
    }

    public boolean isEmpty() {
        return Objects.isNull(body);
    }

    public Optional<String> getBody() {
        return Optional.ofNullable(body);
    }

    public String asString() {
        if(Objects.isNull(body)) {
            return "";
        }

        return body + LINE_SEPARATOR.value();
    }

    public int length() {
        return body.getBytes().length;
    }
}
