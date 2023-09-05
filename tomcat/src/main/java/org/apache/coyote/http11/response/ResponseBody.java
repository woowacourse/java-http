package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.ContentType;

public class ResponseBody {

    public static final ResponseBody EMPTY = new ResponseBody("", ContentType.HTML);

    private String content;
    private ContentType contentType;

    private ResponseBody(String content, ContentType contentType) {
        this.content = content;
        this.contentType = contentType;
    }

    public static ResponseBody of(String body, String fileExtension) {
        return new ResponseBody(body, ContentType.parseContentType(fileExtension));
    }

    public String getContent() {
        return content;
    }

    public void setContent(ResponseBody responseBody) {
        this.content = responseBody.content;
        this.contentType = responseBody.contentType;
    }

    public int getLength() {
        return content.getBytes().length;
    }

    public ContentType getContentType() {
        return contentType;
    }
}
