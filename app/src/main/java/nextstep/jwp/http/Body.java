package nextstep.jwp.http;

import static java.util.stream.Collectors.joining;
import static nextstep.jwp.http.Protocol.LINE_SEPARATOR;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;

public class Body {

    private final String body;

    public Body(String body) {
        this.body = body;
    }

    public static Body fromHttpRequest(String httpRequest) {
        return new Body(extractBody(httpRequest));
    }

    public static Body fromFile(File file) {
        try {
            String content = Files.readAllLines(file.toPath()).stream()
                .collect(joining(LINE_SEPARATOR));

            return new Body(content);
        } catch (IOException e) {
            throw new IllegalArgumentException("파일을 읽는데 실패했습니다.", e);
        }
    }

    private static String extractBody(String httpRequest) {
        String bodySignature = LINE_SEPARATOR.repeat(2);

        int offset = httpRequest.indexOf(bodySignature);
        if (offset == -1) {
            return null;
        }

        return httpRequest.substring(offset + bodySignature.length());
    }

    public Optional<String> getBody() {
        return Optional.ofNullable(body);
    }

    public String asString() {
        if(Objects.isNull(body)) {
            return "";
        }

        return LINE_SEPARATOR + body;
    }
}
