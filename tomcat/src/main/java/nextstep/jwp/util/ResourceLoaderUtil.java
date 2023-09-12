package nextstep.jwp.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceLoaderUtil {

    private static final Logger log = LoggerFactory.getLogger(ResourceLoaderUtil.class);
    private static final String DEFAULT_ROOT_PATH = "static";

    private ResourceLoaderUtil() {
    }

    public static String loadContent(String path) {
        if (!path.contains(".")) {
            path += ".html";
        }

        URL resourcePath = ResourceLoaderUtil.class.getClassLoader().getResource(DEFAULT_ROOT_PATH + path);
        String content = "";
        try {
            content = new String(Files.readAllBytes(new File(resourcePath.getFile()).toPath()));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return content;
    }

}
