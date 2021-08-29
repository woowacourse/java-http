package nextstep.jwp.staticresource;

import nextstep.jwp.http.response.ContentType;

public class StaticResource {

    private final ContentType contentType;
    private final String content;

    public StaticResource(ContentType contentType, String content) {
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
