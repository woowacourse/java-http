package org.apache.catalina.dispatcher.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.line.HttpMethod;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StaticHandler implements Handler {

    private static final String STATIC_ROOT = "static";
    private static final String ROOT_PATH = "/";
    private static final String WELCOME_FILE = "/index.html";

    private final Set<String> resourcePaths;

    public StaticHandler() {
        this.resourcePaths = loadResourcePaths();
    }

    private Set<String> loadResourcePaths() {
        URL root = getClass().getClassLoader().getResource(STATIC_ROOT);
        try {
            Path rootPath = Path.of(root.toURI());
            try (Stream<Path> paths = Files.walk(rootPath)) {
                return paths.filter(Files::isRegularFile)
                        .map(path -> toUrlPath(rootPath.relativize(path)))
                        .collect(Collectors.toUnmodifiableSet());
            }
        } catch (IOException | URISyntaxException e) {
            throw new IllegalStateException("정적 리소스 목록을 불러올 수 없습니다.", e);
        }
    }

    private String toUrlPath(Path relativePath) {
        return ROOT_PATH + relativePath.toString().replace(File.separatorChar, '/');
    }

    @Override
    public boolean supports(HttpRequest request) {
        return request.getHttpMethod() == HttpMethod.GET
                && resourcePaths.contains(toResourcePath(request.getPath()));
    }

    @Override
    public String handle(HttpRequest request, HttpResponse response) {
        return toResourcePath(request.getPath());
    }

    private String toResourcePath(String path) {
        if (ROOT_PATH.equals(path)) {
            return WELCOME_FILE;
        }
        return path;
    }

}
