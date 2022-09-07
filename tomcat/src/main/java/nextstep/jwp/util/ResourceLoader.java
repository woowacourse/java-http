package nextstep.jwp.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.controller.MainController;
import org.apache.coyote.http11.model.ContentType;
import org.apache.coyote.http11.model.response.Resource;

public class ResourceLoader {

    public static Resource load(final String url) throws IOException {
        Path path = Path.of(MainController.class.getResource("/static" + url).getPath());
        String body = Files.readString(path);

        ContentType contentType = ContentType.findByExtension(url);

        return new Resource(body, contentType);
    }
}
