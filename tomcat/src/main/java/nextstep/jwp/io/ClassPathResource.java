package nextstep.jwp.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

public class ClassPathResource {

    public String getStaticContent(final String url) {
        URL resource = getClass().getClassLoader().getResource("static" + url);
        try {
            File file = new File(Objects.requireNonNull(resource).getFile());
            byte[] fileContents = Files.readAllBytes(file.toPath());
            return new String(fileContents);
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException();
        }
    }
}
