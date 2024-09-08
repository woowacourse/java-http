package com.techcourse.controller;


import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;

import java.io.IOException;

public interface Controller {

    HttpResponse execute(HttpRequest httpRequest) throws IOException;
}
