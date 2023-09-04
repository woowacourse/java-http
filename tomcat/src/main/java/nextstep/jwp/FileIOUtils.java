package nextstep.jwp;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileIOUtils {

    private FileIOUtils() {
    }

    public static Path getPath(String resourcePath) {
        try{
            return Path.of(Thread.currentThread()
                    .getContextClassLoader()
                    .getResource(resourcePath).toURI());
        }catch (NullPointerException | URISyntaxException e){
            return null;
        }
    }

    public static byte[] getFileInBytes(String resourcePath) throws IOException {
        return Files.readAllBytes(getPath(resourcePath));
    }
}
