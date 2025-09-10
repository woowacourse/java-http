package org.apache.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;

public class BasicController implements Controller {

    @Override
    public boolean isProcessable(HttpRequest httpRequest) {
        return httpRequest.pathEquals("/");
    }

    @Override
    public Map<String, Object> process(HttpRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();

        response.put("responseBody", "Hello world!");
        response.put("status", HttpStatus.OK);

        return response;
    }
}
