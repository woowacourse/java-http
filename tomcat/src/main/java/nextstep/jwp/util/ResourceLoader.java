package nextstep.jwp.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.exception.NotFoundException;

public class ResourceLoader {

    private static final String STATIC_PATH = "static";

    public static String getStaticResource(String path) throws IOException, URISyntaxException {
        try {
            final URI resource = Thread.currentThread()
                    .getContextClassLoader()
                    .getResource(STATIC_PATH + path)
                    .toURI();
            return new String(Files.readAllBytes(Path.of(resource)));
        } catch (NullPointerException e) {
            throw new NotFoundException("파일을 찾을 수 없습니다.");
        }
    }
}
