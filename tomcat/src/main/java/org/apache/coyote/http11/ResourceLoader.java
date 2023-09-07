package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpStatus.BAD_REQUEST;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class ResourceLoader {

    public String load(String path) throws IOException {
        URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new HttpException(BAD_REQUEST, "요청받은 리소스가 존재하지 않습니다");
        }
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
