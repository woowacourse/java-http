package org.apache.catalina.connector;

import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.RequestMapping;
import org.apache.coyote.Adapter;
import org.apache.coyote.http.Header;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatalinaAdapter implements Adapter {

    private static final Logger log = LoggerFactory.getLogger(CatalinaAdapter.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        try {
            Controller controller = RequestMapping.getController(request);
            controller.service(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response.setHeader(Header.LOCATION.value(), "/500.html");
        }
    }
}
