package servlet;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.MimeType;
import org.apache.coyote.response.Response;
import org.apache.coyote.response.ResponseBody;
import org.apache.coyote.response.ResponseHeaders;
import org.apache.coyote.response.ResponseLine;
import servlet.handler.Handler;
import servlet.handler.HandlerMapping;
import servlet.handler.SimpleUrlHandlerMapping;
import servlet.resolver.ViewResolver;

public class HttpServlet {

    private final List<HandlerMapping> handlerMappings;

    private final ViewResolver viewResolver;

    public HttpServlet() {
        List<HandlerMapping> handlerMappings = new ArrayList<>();
        handlerMappings.add(new SimpleUrlHandlerMapping());
//        handlerMappings.add(new RequestMappingHandlerMapping());
        this.handlerMappings = handlerMappings;
        this.viewResolver = new ViewResolver();
    }

    public Response service(Request request) throws IOException { // todo httpRequest, httpResponse
        Handler handler = getHandler(request);
        ResponseAndView responseAndView = handler.handlerRequest(request);

        File view = viewResolver.resolveViewName(responseAndView.getViewName());
        ResponseLine responseLine = new ResponseLine(responseAndView.getStatusCode());
        ResponseHeaders headers = new ResponseHeaders();
        ResponseBody body = new ResponseBody(Files.readString(view.toPath(), StandardCharsets.UTF_8));
        headers.contentType(MimeType.from(view.getName()).getType());
        headers.contentLength(body.length());
        return new Response(responseLine, headers, body);
    }

    private Handler getHandler(Request request) {
        // todo not found handler
        return handlerMappings.stream()
                .map(mapping -> mapping.getHandler(request))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Not found handler"));
    }
}
