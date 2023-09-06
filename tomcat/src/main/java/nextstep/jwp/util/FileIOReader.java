package nextstep.jwp.util;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileIOReader {

    private FileIOReader() {

    }

    public static String readFile(String url) {
        url = ViewResolver.resolve(url);
        String path = FileIOReader.class.getClassLoader().getResource(url).getPath();
        File file = new File(path);
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new IllegalArgumentException("파일을 읽을 수 없습니다.");
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
