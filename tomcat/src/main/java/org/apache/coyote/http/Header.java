package org.apache.coyote.http;

import static org.apache.coyote.PageMapper.isCustomFileRequest;


public class Header {

    private ContentType contentType;

    public Header(String url) {
        if(isCustomFileRequest(url)){
            this.contentType = ContentType.HTML;
        }
        this.contentType = ContentType.from(url.substring(url.lastIndexOf(".")+1));
    }

    public String getContentType() {
        return contentType.getValue();
    }
}
