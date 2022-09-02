package nextstep.jwp.model;

import java.io.File;
import java.io.IOException;
import nextstep.jwp.http.common.ContentType;

public class StaticResource {

    private final Content content;
    private final ContentType contentType;

    public StaticResource(final Content content, final ContentType contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    public static StaticResource from(File file) throws IOException {
        Content content = Content.readFile(file);
        ContentType contentType = ContentType.from(parseExtension(file.getPath()));

        return new StaticResource(content, contentType);
    }

    private static String parseExtension(final String path) {
        String[] pathValues = path.split("\\.");

        return pathValues[pathValues.length - 1];
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
