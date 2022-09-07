package org.apache.coyote;

import java.io.File;
import java.net.URL;

public class StaticFile {

    public static File findByPath(String url) {
        final URL resource = StaticFile.class.getClassLoader().getResource("static" + url);
        if (resource == null || url.equals("/")) {
            return new File(StaticFile.class.getClassLoader().getResource("static/404.html").getFile());
        }
        return new File(resource.getFile());
    }
}
