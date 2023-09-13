package org.apache.coyote.http11.response;

public class ResponseBody {
    public static final ResponseBody EMPTY = new ResponseBody(new byte[]{}, ContentType.EMPTY);

    private final byte[] content;
    private final ContentType contentType;

    private ResponseBody(final byte[] content, final ContentType contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    public static ResponseBody from(final StaticResource staticResource) {
        return new ResponseBody(
                staticResource.getContent(),
                ContentType.from(staticResource.getFileType())
        );
    }

    public byte[] getContent() {
        return content;
    }

    public int getLength() {
        return content.length;
    }

    public ContentType getContentType() {
        return contentType;
    }
}
