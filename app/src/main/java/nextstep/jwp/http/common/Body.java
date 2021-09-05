package nextstep.jwp.http.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import nextstep.jwp.exception.UnprocessableEntityException;
import nextstep.jwp.http.request.HttpHeaders;

public class Body {

    private static final Body EMPTY_BODY = new Body("");

    private final String value;

    public Body(String value) {
        this.value = value;
    }

    public static Body parse(BufferedReader bufferedReader, HttpHeaders headers)
        throws IOException {
        if (headers.hasRequestBody()) {
            int contentLength = headers.getContentLength();
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);

            return new Body(String.valueOf(buffer));
        }

        return EMPTY_BODY;
    }

    public static Body parse(String path) {
        try {
            URL resource = Objects.requireNonNull(ClassLoader.getSystemResource(path));
            File file = new File(resource.getPath());
            String content = new String(Files.readAllBytes(file.toPath()));
            return new Body(content);
        } catch (IOException e) {
            throw new UnprocessableEntityException();
        }
    }

    public static Body empty() {
        return EMPTY_BODY;
    }

    public int length() {
        return value.getBytes().length;
    }

    public String asString() {
        if (Objects.isNull(value)) {
            return "";
        }
        return value;
    }
}
