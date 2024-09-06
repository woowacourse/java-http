package com.techcourse.infrastructure;

import com.techcourse.presentation.DefaultPresentation;
import com.techcourse.presentation.LoginPresentation;
import http.HttpMethod;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class PresentationResolver {

    private static final DefaultPresentation defaultPresentation = new DefaultPresentation();

    List<Presentation> presentations;

    public PresentationResolver(List<Presentation> presentations) {
        this.presentations = presentations;
    }

    public PresentationResolver() {
        this(List.of(new LoginPresentation()));
    }

    public void view(HttpMethod method, String path, String queryParam) throws UnsupportedEncodingException {
        presentations.stream()
                .filter(presentation -> presentation.match(method, path))
                .findFirst()
                .orElse(defaultPresentation)
                .view(queryParam);
    }
}
