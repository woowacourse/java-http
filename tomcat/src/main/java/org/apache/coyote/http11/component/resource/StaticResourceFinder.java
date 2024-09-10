package org.apache.coyote.http11.component.resource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class StaticResourceFinder {

    private final URL resourcePath;

    public StaticResourceFinder(final String resourceName) {
        this.resourcePath = getClass().getClassLoader().getResource("static/" + resourceName);
    }

    public byte[] getBytes() {
        try {
            return Files.readAllBytes(new File(resourcePath.getFile()).toPath());
        } catch (IOException e) {
            throw new IllegalStateException("존재하지 않는 정적 리소스");
        }
    }
}
