package org.apache.controller;

import java.util.ArrayList;
import java.util.List;

public class HandlerContainer {

    private static List<Controller> handlers = new ArrayList<>();

    public static void add(Controller controller) {
        handlers.add(controller);
    }

    public static List<Controller> getHandlers() {
        return handlers;
    }
}
