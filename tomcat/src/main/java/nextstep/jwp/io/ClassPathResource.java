package nextstep.jwp.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class ClassPathResource {

    private URL resource;

    public ClassPathResource(final String url) {
        this.resource = getClass().getClassLoader().getResource("static" + url);
    }

    public String getFileContents() {
        try {
            File file = new File(resource.getFile());
            byte[] fileContents = Files.readAllBytes(file.toPath());
            return new String(fileContents);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
