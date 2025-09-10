package org.apache.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public interface Controller {

    boolean isProcessable(String path);

    Map<String, Object> process(Map<String, String> requests) throws URISyntaxException, IOException;
}
