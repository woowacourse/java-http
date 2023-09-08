package nextstep.jwp.util;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import org.apache.coyote.http11.response.HttpResponse;

public class PathUtil {

    private static final String STATIC = "static";

    public static Path findPath(final String uri) {
        final URL url = HttpResponse.class.getClassLoader()
                .getResource(STATIC + uri);

        return new File(url.getPath()).toPath();
    }

    public static Path findPathWithExtension(final String uri, final String extension) {
        return findPath(uri + extension);
    }
}
