package servlet;

import java.io.BufferedReader;
import java.io.IOException;
import nextstep.jwp.controller.exception.BaseHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.element.HttpRequestBody;
import org.apache.coyote.http11.request.element.HttpRequestHeader;
import org.apache.coyote.http11.response.HttpResponse;
import servlet.mapping.ExceptionMapping;
import servlet.mapping.ExceptionMappingImpl;
import servlet.mapping.HandlerMapping;
import servlet.mapping.HandlerMappingImpl;
import servlet.mapping.ResponseEntity;
import servlet.view.ViewResolver;

public class ServletImpl implements Servlet {

    private final ExceptionMapping exceptionMapping;
    private final HandlerMapping handlerMapping;
    private final ViewResolver viewResolver;

    public ServletImpl(ExceptionMapping exceptionMapping, HandlerMapping handlerMapping,
                       ViewResolver viewResolver) {
        this.exceptionMapping = exceptionMapping;
        this.handlerMapping = handlerMapping;
        this.viewResolver = viewResolver;
    }

    public static Servlet create() {
        Config config = Config.get();
        HandlerMapping handlerMapping = new HandlerMappingImpl(config.getControllers());
        ViewResolver viewResolver = new ViewResolver();
        ExceptionMapping exceptionMapping = new ExceptionMappingImpl(config.getExceptionHandlers(), new BaseHandler());
        return new ServletImpl(exceptionMapping, handlerMapping, viewResolver);
    }

    @Override
    public String doService(BufferedReader reader, String input) throws IOException {
        HttpRequestHeader requestHeader = HttpRequestHeader.of(input);
        HttpRequestBody requestBody = getHttpRequestBody(reader, requestHeader.find("Content-Length"));
        HttpRequest request = new HttpRequest(requestHeader, requestBody);

        ResponseEntity entity;
        try {
            entity = handlerMapping.map(request);
        } catch (Exception e) {
            entity = exceptionMapping.map(e);
        }
        HttpResponse httpResponse = viewResolver.getResponse(entity.getUri(), entity.getStatus(), entity.getHeaders());
        return httpResponse.getResponse();
    }

    private HttpRequestBody getHttpRequestBody(BufferedReader reader, String rawBodyLength) throws IOException {
        if (rawBodyLength == null) {
            return HttpRequestBody.empty();
        }
        int contentLength = Integer.parseInt(rawBodyLength);
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new HttpRequestBody(new String(buffer));
    }
}
