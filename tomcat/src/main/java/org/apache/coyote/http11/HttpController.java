package org.apache.coyote.http11;

import org.apache.coyote.http11.parser.HttpResponse;
import org.apache.coyote.http11.service.HelloService;
import org.apache.coyote.http11.service.HttpService;
import org.apache.coyote.http11.service.SignService;
import org.apache.coyote.http11.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class HttpController {

    private final static Map<String, HttpService> serviceMap = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(HttpController.class);

    private final HttpRequests httpRequests;
    private final StaticService staticService = new StaticService();

    static {
        serviceMap.put("/", new HelloService());
        serviceMap.put("/login", new UserService());
        serviceMap.put("/register", new SignService());
    }

    public HttpController(HttpRequests httpRequests) {
        this.httpRequests = httpRequests;
    }

    public HttpResponse doRequest() {
        HttpService httpService = serviceMap.get(httpRequests.getHttpRequest());
        HttpResponse httpResponse = new HttpResponse();

        log.info(httpRequests.getHttpRequest());

        if (serveStatic(httpRequests, httpResponse)) {
            return httpResponse;
        }

        if (!isExistHttpService(httpService, httpResponse)) {
            httpResponse.setStatusLine("HTTP/1.1 404 FOUND");
            httpResponse.setLocation("/404.html");
            return httpResponse;
        }

        String method = httpRequests.getMethod();
        if (AcceptableRequest.isPost(method)) {
            httpService.doPost(httpRequests, httpResponse);
        } else if (AcceptableRequest.isGet(method)) {
            httpService.doGet(httpRequests, httpResponse);
        } else if (AcceptableRequest.isDelete(method)) {
            httpService.doDelete(httpRequests, httpResponse);
        }
        return httpResponse;
    }

    private boolean serveStatic(HttpRequests httpRequests, HttpResponse httpResponse) {
        String httpRequest = httpRequests.getHttpRequest();
        if (httpRequest.endsWith(".css") || httpRequest.endsWith(".js") || httpRequest.endsWith(".html")) {
            staticService.doGet(httpRequests, httpResponse);
            return true;
        }

        return false;
    }

    private boolean isExistHttpService(HttpService httpService, HttpResponse httpResponse) {
        if (httpService == null) {
            return false;
        }
        return true;
    }
}
