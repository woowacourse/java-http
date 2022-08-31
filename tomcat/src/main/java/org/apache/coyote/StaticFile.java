package org.apache.coyote;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class StaticFile {

    public static File findByUrl(String url) {
        System.out.println(url);
        List<String> fileNames = getStaticFileNames();
        if (fileNames.contains(url)) {
            final URL resource = StaticFile.class.getClassLoader().getResource("static/" + url);
            return new File(resource.getFile());
        }
        final URL resource = StaticFile.class.getClassLoader().getResource("static/404.html");
        return new File(resource.getFile());
    }

    private static List<String> getStaticFileNames() {
        File directory = new File(StaticFile.class.getClassLoader().getResource("static/").getFile());
        List<String> fileNames = Arrays.asList(directory.list());
        return fileNames;
    }
}
