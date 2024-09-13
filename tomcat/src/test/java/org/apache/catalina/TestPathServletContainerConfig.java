package org.apache.catalina;

import java.util.Map;

import org.apache.catalina.servlet.AbstractController;
import org.apache.catalina.servlet.RequestMapping;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.apache.coyote.http11.httpmessage.response.StaticResource;

import com.techcourse.controller.StaticResourceController;

public class TestPathServletContainerConfig {

    public static final String SUCCESS_PATH = "/success";

    public static RequestMapping getRequestMapping() {
        return new RequestMapping(
                Map.of(
                        SUCCESS_PATH, new SuccessController()
                )
        );
    }

    private static class SuccessController extends AbstractController {

        @Override
        protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
            response.setResponseOfStaticResource(new StaticResource("/index.html"));
        }

        @Override
        protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
            response.setResponseOfStaticResource(new StaticResource("/index.html"));
        }
    }
}
