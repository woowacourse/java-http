package org.apache.catalina.webresources;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.tomcat.util.http.ResourceURI;

public record FileResource(File file) implements WebResource {

    public static final ResourceURI NOT_FOUND_RESOURCE_URI = new ResourceURI("/404.html");
    public static final ResourceURI UN_AUTHORIZED_RESOURCE_URI = new ResourceURI("/401.html");

    @Override
    public boolean exists() {
        return file.exists();
    }

    @Override
    public String getContent() {
        try {
            Path filePath = file.toPath();
            return Files.readString(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getContentType() {
        try {
            Path path = file.toPath();
            return Files.probeContentType(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
