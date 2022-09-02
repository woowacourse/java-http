package org.apache.coyote.http;

import static org.apache.coyote.PageMapper.isCustomFileRequest;


public class Header {

    private static final String DOT = ".";
    private ContentType contentType;

    public Header(final String url) {
        if(isCustomFileRequest(url)){
            this.contentType = ContentType.HTML;
        }
        this.contentType = ContentType.from(url.substring(url.lastIndexOf(DOT)+1));
    }

    public String getContentType() {
        return contentType.getValue();
    }
}
