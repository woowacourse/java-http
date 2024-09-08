package org.apache.coyote.http11.response.view;

import java.util.Map;
import org.apache.coyote.http11.response.HttpStatus;

public class RedirectView implements View {
    private static final String CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String CONTENT = "";

    private final String location;

    public RedirectView(String location) {
        this.location = location;
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
        return Map.of("Location", location);
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

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public RedirectView build() {
            return new RedirectView(location);
        }
    }
}
