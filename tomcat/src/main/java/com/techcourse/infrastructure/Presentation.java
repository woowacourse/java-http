package com.techcourse.infrastructure;

import http.HttpMethod;

public interface Presentation {
    void view(String queryParam);

    boolean match(HttpMethod method, String path);
}
