package org.apache.coyote.view;

import org.apache.coyote.response.HttpResponse;

public interface ViewResolver {

    void resolve(String fileName, HttpResponse response);
}
