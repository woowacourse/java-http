package org.apache.coyote.controller;

import org.apache.coyote.http11.HttpRequestHeader;
import org.apache.coyote.view.ModelAndView;

public abstract class Controller {

    public abstract ModelAndView process(HttpRequestHeader request);
}
