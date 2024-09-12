package org.apache.catalina.connector;

import java.util.NoSuchElementException;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.RequestMapping;
import org.apache.coyote.Adapter;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatalinaAdapter implements Adapter {

    private static final String SERVER_ERROR_PAGE = "/500.html";

    private static final Logger log = LoggerFactory.getLogger(CatalinaAdapter.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        try {
            String uri = request.getUri();
            Controller controller = RequestMapping.getController(uri);
            controller.service(request, response);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            response.sendRedirect(request.getUri());
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            response.sendRedirect(SERVER_ERROR_PAGE);
        }
    }
}
