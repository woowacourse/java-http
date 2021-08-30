package nextstep.jwp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileReader {

    private static final String STATIC_RESOURCE = "./static";

    public static String file(String uri) throws IOException {
        ContentType contentType = ContentType.findBy(uri);
        final URL resource = FileReader.class
                .getClassLoader()
                .getResource(STATIC_RESOURCE + withExtension(contentType, uri));
        final Path path = new File(resource.getFile()).toPath();
        return Files.readString(path);
    }

    private static String withExtension(ContentType contentType, String uri) {
        if (ContentType.NONE.equals(contentType)) {
            return uri + ".html";
        }
        return uri;
    }
}
