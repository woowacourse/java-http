package nextstep.jwp.http.util;

import java.io.File;

public class FileReader {

    private static final String PREFIX = "static";

    public static File getFile(final String path) {
        File file = new File(Thread.currentThread()
            .getContextClassLoader()
            .getResource(PREFIX + path).getFile());

        return file;
    }
}
