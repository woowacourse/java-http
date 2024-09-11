package org.apache.coyote.view;

import org.apache.coyote.response.HttpResponse;

public interface View {

    void render(HttpResponse response);
}
