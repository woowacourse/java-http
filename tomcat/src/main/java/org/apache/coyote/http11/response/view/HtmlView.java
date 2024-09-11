package org.apache.coyote.http11.response.view;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.util.StaticFileUtils;

public class HtmlView implements View {

    private final HttpStatus status;
    private final Map<String, String> addedHeaders;
    private final String body;

    public HtmlView(HttpStatus status, Map<String, String> addedHeaders, String body) {
        this.status = status;
        this.addedHeaders = Map.copyOf(addedHeaders);
        this.body = body;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public Map<String, String> getAddedHeaders() {
        return addedHeaders;
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
        private Map<String, String> addedHeaders;
        private String content;

        public Builder() {
            this.status = HttpStatus.OK;
            this.addedHeaders = new HashMap<>();
            this.content = "";
        }

        public Builder status(HttpStatus status) {
            this.status = status;
            return this;
        }

        public Builder addHeader(String key, String value) {
            addedHeaders.put(key, value);
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
            return new HtmlView(status, addedHeaders, content);
        }
    }
}
