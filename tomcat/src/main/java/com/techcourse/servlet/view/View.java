package com.techcourse.servlet.view;

import org.apache.coyote.response.HttpResponse;

public interface View {

    void render(HttpResponse response);
}
