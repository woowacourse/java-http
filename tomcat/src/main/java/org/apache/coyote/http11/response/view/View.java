package org.apache.coyote.http11.response.view;

import java.util.Map;
import org.apache.coyote.http11.response.HttpStatus;

public interface View {

    HttpStatus getStatus();

    Map<String, String> getAddedHeaders();

    String getContentType();

    String getResponseBody();

    static HtmlView.Builder htmlBuilder() {
        return HtmlView.builder();
    }

    static View redirect(String location) {
        return RedirectView.builder()
                .location(location)
                .build();
    }
}
