package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpExtensionType;

public class ResponseBody {

    private final HttpExtensionType httpExtensionType;
    private final String content;

    private ResponseBody(final HttpExtensionType httpExtensionType, final String content) {
        this.httpExtensionType = httpExtensionType;
        this.content = content;
    }

    public static ResponseBody html(final String content) {
        return new ResponseBody(HttpExtensionType.HTML, content);
    }

    public static ResponseBody of(final String extension, final String content) {
        return new ResponseBody(HttpExtensionType.from(extension), content);
    }

    public HttpExtensionType getHttpExtensionType() {
        return httpExtensionType;
    }

    public String getContent() {
        return content;
    }

    public int getLength() {
        return content.getBytes().length;
    }
}
