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

public class ResourceLocator {

    public File findResource(String fileName) {
        if (Objects.isNull(fileName) || fileName.isBlank()) {
            throw new IllegalArgumentException("파일명은 null 혹은 빈칸일 수 없습니다.");
        }

        URL rootUrl = getClass().getClassLoader().getResource("static");
        if (Objects.isNull(rootUrl)) {
            throw new IllegalArgumentException("static 디렉토리가 존재하지 않습니다.");
        }

        return new File(getAllFiles(new ArrayList<>(), Path.of(rootUrl.getPath())).stream()
                .filter(file -> file.contains(fileName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 파일이 존재하지 않습니다. fileName = " + fileName)));
    }

    private List<String> getAllFiles(List<String> fileNames, Path dir) {
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
