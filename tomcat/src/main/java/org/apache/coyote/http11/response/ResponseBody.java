package org.apache.coyote.http11.response;

public class ResponseBody {
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

    public static ResponseBody noContent(final ContentType contentType) {
        return new ResponseBody(new byte[]{}, contentType);
    }

    public static ResponseBody rootContent() {
        return new ResponseBody("Hello world!".getBytes(), ContentType.HTML);
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
