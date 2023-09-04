package nextstep.jwp;

import java.net.URISyntaxException;
import java.nio.file.Path;

public class FileIOUtils {

    public static Path getPath(String resourcePath) {
        try{
            return Path.of(Thread.currentThread()
                    .getContextClassLoader()
                    .getResource(resourcePath).toURI());
        }catch (NullPointerException | URISyntaxException e){
            return null;
        }
    }
}
