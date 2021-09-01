package nextstep.jwp.ui.common;

import nextstep.jwp.exception.NotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class ResourceFile {

    private final String path;

    public ResourceFile(String path) {
        this.path = "static" + path;
    }

    public String getContent() {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(path);
        if (Objects.isNull(resourceAsStream)) {
            throw new NotFoundException();
        }
        try {
            return new String(resourceAsStream.readAllBytes());
        } catch (IOException e) {
            throw new NotFoundException();
        }
    }

    public String getContentType() {
        return ContentType.findContentType(path);
    }
}
