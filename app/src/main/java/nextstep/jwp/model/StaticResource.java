package nextstep.jwp.model;

public class StaticResource {

    private final Content content;
    private final ContentType contentType;

    public StaticResource(Content content, ContentType contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    public static StaticResource from(String contentPath) {
        Content content = new Content(contentPath);
        ContentType contentType = ContentType.findByExtension(parseExtension(contentPath));

        return new StaticResource(content, contentType);
    }

    public static String parseExtension(String contentPath) {
        String[] splitedPath = contentPath.split(".");

        return splitedPath[splitedPath.length - 1];
    }

    public String getContent() {
        return content.getValue();
    }

    public String getContentType() {
        return contentType.getType();
    }

    public String getContentLength() {
        return String.valueOf(content.getLength());
    }
}
