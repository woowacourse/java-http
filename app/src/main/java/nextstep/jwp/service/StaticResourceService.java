package nextstep.jwp.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import nextstep.jwp.model.StaticResource;

public class StaticResourceService {

    private static final String PREFIX = "static";

    public StaticResource findByPath(String path) throws IOException {
        URL url = ClassLoader.getSystemResource(PREFIX + path);
        File file = new File(url.getFile());

        return StaticResource.from(file);
    }
}
