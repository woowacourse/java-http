package org.apache.catalina.startup;

import com.techcourse.controller.HttpController;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Controllers {
    private final Map<String, HttpController> value;

    public Controllers(HttpController... controller) {
        value = Arrays.stream(controller)
                .collect(Collectors.toMap(HttpController::getPath, Function.identity()));
    }

    boolean contains(String path) {
        return value.containsKey(path);
    }

    public HttpController get(String path) {
        return value.get(path);
    }
}
