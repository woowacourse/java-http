package servlet;

import com.techcourse.presentation.LoginController;
import com.techcourse.presentation.RegisterController;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import servlet.handler.Handler;
import servlet.handler.ResourceHandler;
import servlet.handler.WelcomeHandler;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.MimeType;
import org.apache.coyote.http.StatusCode;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.response.Response;
import servlet.resolver.ViewResolver;

public class HttpServlet {

    private final List<RequestMappingInfo> requestMappingInfos;

    private final ViewResolver viewResolver;

    public HttpServlet() {
        this.requestMappingInfos = List.of(
                new RequestMappingInfo("/login", HttpMethod.GET, LoginController.getInstance()::getLogin),
                new RequestMappingInfo("/login", HttpMethod.POST, LoginController.getInstance()::postLogin),
                new RequestMappingInfo("/register", HttpMethod.POST, RegisterController.getInstance()::register),
                new RequestMappingInfo("/", HttpMethod.GET, new WelcomeHandler())
        );
        this.viewResolver = new ViewResolver();
    }

    public void service(Request request, Response response) throws IOException {
        Handler handler = getHandler(request);
        handler.handleRequest(request, response);
        File view = viewResolver.resolveViewName(response.getViewName());
        if (view == null) {
            response.configureViewAndStatus("/404", StatusCode.NOT_FOUND);
            view = viewResolver.resolveViewName(response.getViewName());
        }
        MimeType mimeType = MimeType.from(view.getName());
        response.setContentType(mimeType);
        response.setBody(Files.readString(view.toPath(), StandardCharsets.UTF_8));
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
