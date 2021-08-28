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
        if (path.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/plain";
    }
}
