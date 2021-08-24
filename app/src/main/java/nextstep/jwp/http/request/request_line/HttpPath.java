package nextstep.jwp.http.request.request_line;

import java.io.File;

public class HttpPath {
    private final String path;

    public HttpPath(String path) {
        if(path.startsWith("/")) {
            path = path.substring(1);
        }
        this.path = ClassLoader.getSystemResource(path).getPath();;
    }

    public File toFile() {
        return new File(path);
    }
}
