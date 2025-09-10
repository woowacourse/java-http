package org.apache.coyote.http11;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResourceLoader {
    public static byte[] findResource(String resourcePath) throws IOException {
        if (resourcePath.equals("/")) {
            return "Hello world!".getBytes(StandardCharsets.UTF_8);
        }

        int index = resourcePath.lastIndexOf("/");
        String resource =  resourcePath.substring(index + 1);
        if (resource.contains(".")) {
            resourcePath = "./static" + resourcePath;
        } else {
            resourcePath = "./static" + resourcePath + ".html";
        }

        URL systemResource = ClassLoader.getSystemResource(resourcePath);

        if (systemResource == null) {
            throw new IllegalArgumentException();
        }

        Path path = Path.of(systemResource.getPath());
        return Files.readAllBytes(path);
    }

    public static MimeType getMimeType(String resourcePath) {
        String extension = ".html";
        if (resourcePath.equals("/")) {
            return MimeType.getMimeType(extension);
        }

        int index = resourcePath.lastIndexOf("/");
        String resource =  resourcePath.substring(index + 1);

        if (resource.contains(".")) {
            int dotIndex = resource.lastIndexOf(".");
            extension = resource.substring(dotIndex);
            return MimeType.getMimeType(extension);
        }
        return MimeType.getMimeType(".html");
    }
}
