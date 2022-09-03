package nextstep.jwp.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import nextstep.jwp.model.StaticResource;

public class StaticResourceUtil {

    private static final String PREFIX = "static";

    public static StaticResource findByPath(String path) throws IOException {
        try {
            URL url = Thread.currentThread().getContextClassLoader().getResource(PREFIX + path);
            File file = new File(url.getFile());

            return StaticResource.from(file);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static StaticResource findByPathWithExtension(String path, String extension) throws IOException{
        return findByPath(path + extension);
    }
}
