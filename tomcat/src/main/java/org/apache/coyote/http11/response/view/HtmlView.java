package org.apache.coyote.http11.response.view;

import java.util.Collections;
import java.util.Map;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.util.StaticFileUtils;

public class HtmlView implements View {

    private final HttpStatus status;
    private final String body;

    public HtmlView(HttpStatus status, String body) {
        this.status = status;
        this.body = body;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.OK;
    }

    @Override
    public Map<String, String> getAddedHeaders() {
        return Collections.EMPTY_MAP;
    }

    @Override
    public String getContentType() {
        return "text/html;charset=utf-8";
    }

    @Override
    public String getResponseBody() {
        return body;
    }

    public static class Builder {

        private HttpStatus status;
        private String content;

        public Builder() {
            this.status = HttpStatus.OK;
            this.content = "";
        }

        public Builder status(HttpStatus status) {
            this.status = status;
            return this;
        }

        public Builder staticResource(String path) {
            this.content = StaticFileUtils.readStaticFile(path);
            return this;
        }

        public Builder text(String content) {
            this.content = content;
            return this;
        }

        public HtmlView build() {
            return new HtmlView(status, content);
        }
    }
}
