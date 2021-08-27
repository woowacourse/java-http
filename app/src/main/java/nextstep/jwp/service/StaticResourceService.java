package nextstep.jwp.service;

import java.net.URL;
import nextstep.jwp.model.StaticResource;

public class StaticResourceService {

    private static final String PREFIX = "static";

    public StaticResource findByPath(String path) {
        URL url = ClassLoader.getSystemResource(PREFIX + path);

        return StaticResource.from(url.getPath());
    }
}
