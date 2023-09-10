package nextstep.jwp.common;

import static nextstep.jwp.exception.ResourceExceptionType.RESOURCE_NOT_FOUND;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.exception.ResourceException;

public class ResourceLoader {

    public String load(String path) {
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new ResourceException(RESOURCE_NOT_FOUND);
        }
        File file = new File(resource.getFile());
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            return new String(bytes);
        } catch (IOException e) {
            throw new ResourceException(RESOURCE_NOT_FOUND);
        }
    }
}
