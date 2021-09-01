package nextstep.joanne.server.converter;

import nextstep.joanne.dashboard.exception.FileErrorException;
import nextstep.joanne.dashboard.exception.FileNotFoundException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class FileConverter {
    public String getResource(String uri) {
        if (!uri.contains(".")) {
            uri += ".html";
        }
        URL resource = this.getClass().getClassLoader().getResource("static" + uri);
        if (Objects.isNull(resource)) {
            throw new FileNotFoundException();
        }
        final Path filePath = new File(resource.getPath()).toPath();

        try {
            return Files.readString(filePath);
        } catch (IOException e) {
            throw new FileErrorException();
        }
    }
}
