package study;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Resource {

    private final URL resource;

    private Resource(final String fileName) {
        this.resource = getClass().getClassLoader()
                .getResource(fileName);
    }

    public static Resource newResource(final String fileName) {
        return new Resource(fileName);
    }

    public List<String> read() {
        try {
            final Path path = new File(resource.getPath()).toPath();
            return Files.readAllLines(path);
        } catch (final IOException e) {
            throw new RuntimeException();
        }
    }

    public String getPath() {
        return resource.getPath();
    }
}
