package org.apache.coyote.http11.response;

public class ResponseBody {
    private final String content;
    private final ContentType contentType;

    private ResponseBody(final String content, final ContentType contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    public static ResponseBody from(final StaticResource staticResource) {
        return new ResponseBody(
                staticResource.getContent(),
                ContentType.from(staticResource.getFileType())
        );
    }

    public String getContent() {
        return content;
    }

    public ContentType getContentType() {
        return contentType;
    }
}
