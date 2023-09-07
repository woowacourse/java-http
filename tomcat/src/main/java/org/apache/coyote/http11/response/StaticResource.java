package org.apache.coyote.http11.response;

import org.apache.coyote.http11.exception.FileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StaticResource {
    private static final Logger log = LoggerFactory.getLogger(StaticResource.class);

    private final String content;
    private final String fileType;

    private StaticResource(final String content, final String fileType) {
        this.content = content;
        this.fileType = fileType;
    }

    public static StaticResource from(final String fileName) throws IOException {
        return new StaticResource(extractContent(fileName), extractFileExtension(fileName));
    }

    private static String extractContent(final String fileName) throws IOException {
        try {
            final URL resource = HttpResponse.class.getClassLoader().getResource("static" + fileName);
            final Path path = Paths.get(resource.getPath());
            return Files.readString(path);
        } catch (final NullPointerException e) {
            log.error("error fileName = {}", fileName, e);
            throw new FileNotFoundException(fileName, e);
        }
    }

    private static String extractFileExtension(final String fileName) {
        return fileName.split("\\.")[1];
    }

    public String getContent() {
        return content;
    }

    public String getFileType() {
        return fileType;
    }
}
