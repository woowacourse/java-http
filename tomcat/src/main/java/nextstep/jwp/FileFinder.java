package nextstep.jwp;

import java.io.File;
import nextstep.jwp.handler.RequestHandler;

public class FileFinder {

    public static File get(String uri) {
        String fileUrl = "static" + uri;
        return new File(
            RequestHandler.class
                .getClassLoader()
                .getResource(fileUrl)
                .getFile()
        );
    }
}
