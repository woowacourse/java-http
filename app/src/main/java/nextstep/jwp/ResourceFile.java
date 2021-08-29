package nextstep.jwp;

import java.io.IOException;

public class ResourceFile {

    private final String path;

    public ResourceFile(String path) {
        this.path = "static" + path;
    }

    public String getContent() throws IOException {
        return new String(getClass().getClassLoader().getResourceAsStream(path).readAllBytes());
    }

    public String getContentType() {
        return ContentType.findContentType(path);
    }
}
