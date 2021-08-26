package nextstep.jwp.http.response;

import nextstep.jwp.staticresource.StaticResource;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public enum ContentType {
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "application/js"),
    ICO("ico", "image/x-icon");

    private static final String NEW_LINE = System.getProperty("line.separator");

    private final String fileNameExtension;
    private final String value;

    ContentType(String fileNameExtension, String value) {
        this.fileNameExtension = fileNameExtension;
        this.value = value;
    }

    public static StaticResource getStaticResource(String fileNameExtension, URL url) throws URISyntaxException, IOException {
        final Path filePath = Paths.get(url.toURI());
        final ContentType contentType = getContentTypeByFileNameExtension(fileNameExtension);
        if (ICO == contentType) {
            return getICOStaticResource(filePath, contentType);
        }
        return getStaticResourceExceptICO(filePath, contentType);
    }

    private static ContentType getContentTypeByFileNameExtension(String fileNameExtension) {
        return Arrays.stream(ContentType.values())
                .filter(type -> type.hasSameFileNameExtension(fileNameExtension))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("파일 확장자가 존재하지 않습니다."));
    }

    private static StaticResource getICOStaticResource(Path filePath, ContentType contentType) {
        final String content = new File(filePath.toUri()).toString();
        return new StaticResource(contentType, content);
    }

    private static StaticResource getStaticResourceExceptICO(Path filePath, ContentType contentType) throws IOException {
        final List<String> fileLines = Files.readAllLines(filePath);
        final String content = String.join(NEW_LINE, fileLines);
        return new StaticResource(contentType, content);
    }

    private boolean hasSameFileNameExtension(String fileNameExtension) {
        return this.fileNameExtension.equals(fileNameExtension);
    }

    public String getValue() {
        return value;
    }
}
