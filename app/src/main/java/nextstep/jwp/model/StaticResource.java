package nextstep.jwp.model;

import java.io.File;
import java.io.IOException;

public class StaticResource {

    private final Content content;
    private final ContentType contentType;

    public StaticResource(Content content, ContentType contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    public static StaticResource from(File file) throws IOException {
        Content content = Content.readFile(file);
        ContentType contentType = ContentType.findByExtension(parseExtension(file.getPath()));

        return new StaticResource(content, contentType);
    }

    private static String parseExtension(String contentPath) {
        String[] splitedPath = contentPath.split("\\.");

        return splitedPath[splitedPath.length - 1];
    }

    public String getContent() {
        return content.getValue();
    }

    public String getContentLength() {
        return String.valueOf(content.getLength());
    }

    public String getContentType() {
        return contentType.getType();
    }
}
