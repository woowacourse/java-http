package org.apache.catalina.controller;

import java.util.Arrays;
import java.util.List;

public class TestAbstractController extends AbstractController {

    public TestAbstractController(Handler... handlers) {
        this(Arrays.stream(handlers).toList());
    }

    public TestAbstractController(List<Handler> handlers) {
        registerHandlers(handlers);
    }
}
