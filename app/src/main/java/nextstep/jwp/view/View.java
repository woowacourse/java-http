package nextstep.jwp.view;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import nextstep.jwp.http.response.ContentType;

public class View {

    private final String content;
    private final ContentType contentType;

    public View(String content, ContentType contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    public View(String content) {
        this(content, ContentType.empty());
    }

    public static View asString(String content) {
        return new View(content, ContentType.PLAIN_UTF8);
    }

    public static View asFile(File file) throws IOException {
        String fileName = file.getName();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

        String content = String.join("\n", Files.readAllLines(file.toPath())) + "\n";
        ContentType contentType = ContentType.parseFromExtension(extension);

        return new View(content, contentType);
    }

    public static View empty() {
        return new View("");
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }

    public String contentType() {
        return contentType.value();
    }

    public int contentLength() {
        return content.getBytes().length;
    }

    public String content() {
        return content;
    }
}
