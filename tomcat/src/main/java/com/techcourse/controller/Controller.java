package com.techcourse.controller;


import org.apache.catalina.request.HttpRequest;

import java.io.IOException;

public interface Controller {

    String execute(HttpRequest httpRequest) throws IOException;
}
