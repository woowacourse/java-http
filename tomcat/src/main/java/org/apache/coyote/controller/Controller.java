package org.apache.coyote.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.view.ModelAndView;

public abstract class Controller {

    public abstract ModelAndView process(HttpRequest request);
}
