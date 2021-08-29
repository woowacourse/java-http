package nextstep.jwp.util;

public class File {
    ContentType contentType;
    String content;

    public File(ContentType contentType, String content) {
        this.contentType = contentType;
        this.content = content;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public String getContent() {
        return content;
    }
}
