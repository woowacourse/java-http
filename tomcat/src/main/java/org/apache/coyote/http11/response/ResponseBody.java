package org.apache.coyote.http11.response;

import org.apache.coyote.http11.ExtensionType;

public class ResponseBody {

    private final ExtensionType extensionType;
    private final String content;

    private ResponseBody(final ExtensionType extensionType, final String content) {
        this.extensionType = extensionType;
        this.content = content;
    }

    public static ResponseBody html(final String content) {
        return new ResponseBody(ExtensionType.HTML, content);
    }

    public static ResponseBody of(final String extension, final String content) {
        return new ResponseBody(ExtensionType.from(extension), content);
    }

    public ExtensionType getHttpExtensionType() {
        return extensionType;
    }

    public String getContent() {
        return content;
    }

    public int getLength() {
        return content.getBytes().length;
    }
}
