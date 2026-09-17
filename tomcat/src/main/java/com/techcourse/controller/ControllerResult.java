package com.techcourse.controller;

import com.techcourse.controller.ControllerResult.Redirect;
import com.techcourse.controller.ControllerResult.View;

public sealed interface ControllerResult permits View, Redirect {

    record View(String path) implements ControllerResult {}

    record Redirect(String location) implements ControllerResult {}
}
