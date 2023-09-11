package nextstep.jwp.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.exception.FileNotFoundException;

public class ResourceManager {

    private static final String PATH = "static";
    private static final String EXTENSION_SIGN = ".";
    private static final String PREFIX = "/";

    private final File file;

    private ResourceManager(File file) {
        this.file = file;
    }

    public static ResourceManager from(String resourceUrl) {
        if (!resourceUrl.startsWith(PREFIX)) {
            resourceUrl = PREFIX + resourceUrl;
        }
        URL path = ResourceManager.class.getClassLoader().getResource(PATH + resourceUrl);
        validatePath(path);
        return new ResourceManager(new File(path.getFile()));
    }

    private static void validatePath(URL path) {
        if (path == null) {
            throw new FileNotFoundException("존재하지 않는 파일의 경로입니다.");
        }
    }

    public String extractResourceType() {
        String fileName = file.getName();
        int extensionSignIndex = fileName.lastIndexOf(EXTENSION_SIGN);
        return fileName.substring(extensionSignIndex + 1);
    }

    public String readResourceContent() {
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new FileNotFoundException("파일을 불러올 수 없습니다.");
        }
    }
}
