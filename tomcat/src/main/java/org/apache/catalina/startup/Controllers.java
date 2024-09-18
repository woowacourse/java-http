package org.apache.catalina.startup;

import com.techcourse.controller.HttpController;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

public class Controllers {
    private final List<HttpController> value;

    public Controllers(HttpController... controller) {
        value = Arrays.stream(controller).toList();
    }

    boolean contains(String path) {
        return value.stream().anyMatch(r -> r.getPath().equals(path));
    }

    public HttpController get(String path) {
        return value.stream()
                .filter(r -> r.getPath().equals(path))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("Path incorrect"));
    }

}
