package org.apache.catalina.startup;

import com.techcourse.controller.Controller;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Controllers {
    private final List<Controller> value;

    public Controllers(Controller... controller) {
        value = Arrays.stream(controller).toList();
    }


}
