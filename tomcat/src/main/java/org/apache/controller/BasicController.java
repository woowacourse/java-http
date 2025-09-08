package org.apache.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpStatus;

public class BasicController implements Controller {

    @Override
    public boolean isProcessable(final String path) {
        return path.equals("/");
    }

    @Override
    public Map<String, Object> process(final Map<String, String> requests) {
        Map<String, Object> response = new HashMap<>();
        
        response.put("responseBody", "Hello world!");
        response.put("status", HttpStatus.OK);

        return response;
    }
}
