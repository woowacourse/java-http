package com.controller;

import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;

public interface Controller {

    Http11Response control(final Http11Request request);
}
