package org.apache.catalina.util;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.exception.UncheckedServletException;

public class ResourceFileReader {

    private static final ClassLoader classLoader = ClassLoader.getSystemClassLoader();

    public static String readFile(final String path) throws UncheckedServletException {
        try {
            URL resource = classLoader.getResource("static" + path);
            File file = new File(resource.getFile());
            return new String(Files.readAllBytes(file.toPath()));
        } catch (Exception e) {
            throw new UncheckedServletException(e);
        }
    }
}
