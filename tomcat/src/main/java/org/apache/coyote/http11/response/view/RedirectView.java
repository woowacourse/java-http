package org.apache.coyote.http11.response.view;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.response.HttpStatus;

public class RedirectView implements View {
    private static final String CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String CONTENT = "";

    private final String location;
    private final Map<String, String> addedHeaders;

    public RedirectView(String location, Map<String, String> addedHeaders) {
        this.location = location;
        this.addedHeaders = new HashMap<>(addedHeaders);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.FOUND;
    }

    @Override
    public Map<String, String> getAddedHeaders() {
        Map<String, String> headers = new HashMap<>(addedHeaders);
        headers.put("Location", location);
        return headers;
    }

    @Override
    public String getContentType() {
        return CONTENT_TYPE;
    }

    @Override
    public String getResponseBody() {
        return CONTENT;
    }

    public static class Builder {

        private String location;
        private Map<String, String> addedHeaders;

        private Builder() {
            this.addedHeaders = new HashMap<>();
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder addHeader(String key, String value) {
            addedHeaders.put(key, value);
            return this;
        }

        public RedirectView build() {
            return new RedirectView(location, addedHeaders);
        }
    }
}
