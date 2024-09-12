package org.apache.catalina.controller;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.NoSuchElementException;

public class StaticResourceReader {

    private static final String STATIC_RESOURCE_PATH = "static";
    private static final String NOT_FOUND_PAGE = "/404.html";

    public static byte[] readResource(ClassLoader classLoader, String location) {
        try {
            URL resource = getResourceURL(classLoader, location);
            return Files.readAllBytes(new File(resource.getFile()).toPath());
        } catch (Exception e) {
            throw new NoSuchElementException("리소스가 없습니다.");
        }
    }

    private static URL getResourceURL(ClassLoader classLoader, String location) {
        URL resource = classLoader.getResource(STATIC_RESOURCE_PATH + location);
        if (resource == null) {
            return classLoader.getResource(STATIC_RESOURCE_PATH + NOT_FOUND_PAGE);
        }
        return resource;
    }
}
