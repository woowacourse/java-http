package servlet;

import com.techcourse.presentation.LonginController;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import org.apache.coyote.request.HttpMethod;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.MimeType;
import org.apache.coyote.response.Response;
import org.apache.coyote.response.ResponseBody;
import org.apache.coyote.response.ResponseHeaders;
import org.apache.coyote.response.ResponseLine;
import servlet.handler.Handler;
import servlet.resolver.ViewResolver;
import servlet.handler.ResourceHandler;
import servlet.resource.WelcomePageHandler;

public class HttpServlet {

    private final List<RequestMappingInfo> requestMappingInfos;

    private final ViewResolver viewResolver;

    public HttpServlet() {
        this.requestMappingInfos = List.of(
                new RequestMappingInfo("/login", HttpMethod.GET, new LonginController()::getLoginPage),
                new RequestMappingInfo("/", HttpMethod.GET, new WelcomePageHandler())
        );
        this.viewResolver = new ViewResolver();
    }

    public Response service(Request request) throws IOException { // todo httpRequest, httpResponse
        Handler handler = getHandler(request);
        ResponseAndView responseAndView = handler.handlerRequest(request);

        File view = viewResolver.resolveViewName(responseAndView.getViewName());
        ResponseLine responseLine = new ResponseLine(responseAndView.getStatusCode()); // todo render
        ResponseHeaders headers = new ResponseHeaders();
        ResponseBody body = new ResponseBody(Files.readString(view.toPath(), StandardCharsets.UTF_8));
        headers.contentType(MimeType.from(view.getName()).getType());
        headers.contentLength(body.length());
        return new Response(responseLine, headers, body);
    }

    private Handler getHandler(Request request) {
        return requestMappingInfos.stream()
                .map(requestMappingInfo -> requestMappingInfo.match(request))
                .filter(Objects::nonNull)
                .findFirst()
                .map(RequestMappingInfo::getHandler)
                .orElseGet(ResourceHandler::new);
    }
}
