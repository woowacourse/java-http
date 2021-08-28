package nextstep.jwp.core.handler;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.core.ApplicationContext;
import nextstep.jwp.core.DefaultApplicationContext;
import nextstep.jwp.core.handler.mapping.HandlerMapping;
import nextstep.jwp.core.handler.mapping.MethodHandlerMapping;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.ContentType;
import nextstep.jwp.response.HttpResponse;
import nextstep.jwp.response.StatusCode;

public class FrontHandler {

    private HandlerMapping handlerMapping;
    private ApplicationContext applicationContext;

    public FrontHandler(String basePackage) {
        this.applicationContext = new DefaultApplicationContext(basePackage);
        this.handlerMapping = new MethodHandlerMapping(applicationContext);
    }

    public HttpResponse getResponse(HttpRequest httpRequest, HttpResponse httpResponse) {
        final Handler handler = handlerMapping.findHandler(httpRequest);
        if (handler == null) {
            return resourceHandle(httpRequest, httpResponse);
        }

        final String content = handler.doRequest(httpRequest, httpResponse);
        httpResponse.addBody(content);
        return httpResponse;
    }

    private HttpResponse resourceHandle(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            final String file = httpRequest.httpUrl();
            final URL url = getClass().getClassLoader().getResource("static/" + file);
            byte[] body = Files.readAllBytes(new File(url.toURI()).toPath());
            httpResponse.addHeader("Content-Type", ContentType.contentType(file));
            httpResponse.addBody(new String(body));
            return httpResponse;
        } catch (Exception e) {
            httpResponse.addStatus(StatusCode.NOT_FOUND);
            return httpResponse;
        }
    }

}
