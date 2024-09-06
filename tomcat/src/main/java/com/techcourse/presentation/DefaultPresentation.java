package com.techcourse.presentation;

import com.techcourse.infrastructure.Presentation;
import http.HttpMethod;

public class DefaultPresentation implements Presentation {

    @Override
    public void view(String queryParam) {

    }

    @Override
    public boolean match(HttpMethod method, String path) {
        return false;
    }
}
