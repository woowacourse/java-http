package nextstep.jwp.util;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import nextstep.jwp.exception.ResourceNotFoundException;

public class FileIOReader {

    private FileIOReader() {

    }

    public static String readFile(String url) {
        url = ViewResolver.resolve(url);
        try {
            String path = FileIOReader.class.getClassLoader().getResource(url).getPath();
            File file = new File(path);
            return new String(Files.readAllBytes(file.toPath()));
        } catch (NullPointerException | IOException e) {
            throw new ResourceNotFoundException(url);
        }
    }

    public static class ViewResolver {

        private static final String STATIC = "static";

        private ViewResolver() {

        }

        public static String resolve(String url) {
            if (url.contains(".")) {
                return staticUrl(url);
            } else {
                return staticUrl(url + ".html");
            }
        }

        private static String staticUrl(String url) {
            return STATIC + url;
        }
    }
}
