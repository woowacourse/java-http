package org.apache.coyote.http11.request;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ResourceLocator {

    private static final String STATIC_RESOURCE_DIR_NAME = "static";
    private static final Path ROOT_DIR;
    private static final List<File> RESOURCES;

    static {
        ROOT_DIR = initRootDir();
        RESOURCES = initResources();
    }

    public static File findResource(String uri) {
        if (Objects.isNull(uri) || uri.isBlank()) {
            throw new IllegalArgumentException("자원은 null 혹은 빈칸일 수 없습니다.");
        }

        return RESOURCES.stream()
                .filter(file -> file.getPath().contains(uri))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 파일이 존재하지 않습니다. uri = " + uri));
    }

    private static Path initRootDir() {
        URL rootUrl = ResourceLocator.class.getClassLoader().getResource(STATIC_RESOURCE_DIR_NAME);
        if (Objects.isNull(rootUrl)) {
            throw new IllegalArgumentException("static 디렉토리가 존재하지 않습니다.");
        }
        return Path.of(rootUrl.getPath());
    }

    private static List<File> initResources() {
        return getAllFiles(new ArrayList<>(), ROOT_DIR)
                .stream()
                .map(File::new)
                .collect(Collectors.toList());
    }

    private static List<String> getAllFiles(List<String> fileNames, Path dir) {
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                if(path.toFile().isDirectory()) {
                    getAllFiles(fileNames, path);
                } else {
                    fileNames.add(path.toAbsolutePath().toString());
                }
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        return fileNames;
    }
}
