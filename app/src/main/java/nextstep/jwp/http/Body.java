package nextstep.jwp.http;

import static java.util.stream.Collectors.joining;
import static nextstep.jwp.http.Protocol.LINE_SEPARATOR;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.http.content_type.ContentType;

public class Body {

    private final String body;
    private final String contentType;

    public Body(String body, String contentType) {
        this.body = body;
        this.contentType = contentType;
    }

    public static Body fromHttpRequest(String httpRequest) {
        return new Body(extractBody(httpRequest), extractContentType(httpRequest));
    }

    private static String extractBody(String httpRequest) {
        String bodySignature = LINE_SEPARATOR.repeat(2);

        int offset = httpRequest.indexOf(bodySignature);
        if (offset == -1) {
            return null;
        }

        return httpRequest.substring(offset + bodySignature.length());
    }

    private static String extractContentType(String httpRequest) {
        return Arrays.stream(httpRequest.split(LINE_SEPARATOR))
            .filter(header -> header.contains("Content-Type"))
            .map(header -> header.split(":")[1].trim())
            .findAny()
            .orElse(ContentType.TEXT_PLAIN.asString());
    }

    public static Body fromFile(File file) {
        try {
            String content = Files.readAllLines(file.toPath()).stream()
                .collect(joining(LINE_SEPARATOR));

            return new Body(content, ContentType.from(file));
        } catch (IOException e) {
            throw new IllegalArgumentException("파일을 읽는데 실패했습니다.", e);
        }
    }

    public Optional<String> getBody() {
        return Optional.ofNullable(body);
    }

    public String asString() {
        if(Objects.isNull(body)) {
            return "" + LINE_SEPARATOR;
        }

        return body + LINE_SEPARATOR;
    }

    public int length() {
        return body.getBytes().length;
    }

    public String getContentType() {
        return contentType;
    }
}
