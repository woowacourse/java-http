package org.apache.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import org.apache.http.HttpRequest;

public interface Controller {

    boolean isProcessable(HttpRequest httpRequest);

    Map<String, Object> process(HttpRequest httpRequest) throws URISyntaxException, IOException;
}
