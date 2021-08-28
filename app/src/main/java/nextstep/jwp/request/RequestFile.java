package nextstep.jwp.request;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;

public class RequestFile {

    private final File file;

    public RequestFile(URL url) {
        this(new File(url.getPath()));
    }

    public RequestFile(File file) {
        this.file = file;
    }

    public Path toPath() {
        return file.toPath();
    }

    public File getFile() {
        return file;
    }
}
