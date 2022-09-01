package nextstep.jwp.io;

import java.net.URL;

public class ClassPathResource {

    private URL resource;

    public ClassPathResource(final String url) {
        this.resource = getClass().getClassLoader().getResource("static" + url);
    }

    public String getFile() {
        return resource.getFile();
    }
}
