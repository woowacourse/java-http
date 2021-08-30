package nextstep.jwp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileReader {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final String[] FILENAME_EXTENSION = {"html", "css", "js", "ico", "svg"};
    private static final String STATIC_RESOURCE = "./static";

    public static String file(String uri) throws IOException {
        log.debug("uri" + uri);
        final URL resource = FileReader.class
                .getClassLoader()
                .getResource(STATIC_RESOURCE + withExtension(uri));
        final Path path = new File(resource.getFile()).toPath();
        return Files.readString(path);
    }

    private static String withExtension(String uri) {
        for (String extension : FILENAME_EXTENSION) {
            if (uri.endsWith(extension)) {
                return uri;
            }
        }
        return uri + ".html";
    }
}
