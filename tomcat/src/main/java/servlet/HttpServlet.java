package servlet;

import com.techcourse.presentation.LoginController;
import com.techcourse.presentation.RegisterController;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.MimeType;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.response.Response;
import servlet.handler.Handler;
import servlet.handler.NotFoundHandler;
import servlet.handler.ResourceHandler;
import servlet.handler.WelcomeHandler;
import servlet.resolver.ViewResolver;

public class HttpServlet {

    private static final List<RequestMappingInfo> REQUEST_MAPPING_INFOS = List.of(
            new RequestMappingInfo("/login", HttpMethod.GET, LoginController.getInstance()::getLogin),
            new RequestMappingInfo("/login", HttpMethod.POST, LoginController.getInstance()::postLogin),
            new RequestMappingInfo("/register", HttpMethod.POST, RegisterController.getInstance()::register),
            new RequestMappingInfo("/", HttpMethod.GET, WelcomeHandler.getInstance())
    );

    private static HttpServlet INSTANCE;

    private final List<RequestMappingInfo> requestMappingInfos;

    private final ViewResolver viewResolver;

    private HttpServlet() {
        this.requestMappingInfos = REQUEST_MAPPING_INFOS;
        this.viewResolver = ViewResolver.getInstance();
    }

    public static HttpServlet getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HttpServlet();
        }
        return INSTANCE;
    }

    public void service(Request request, Response response) throws IOException {
        Handler handler = getHandler(request);
        handler.handleRequest(request, response);
        File view = viewResolver.resolveViewName(response.getViewName());
        if (view == null) {
            NotFoundHandler.getInstance().handleRequest(request, response);
            view = viewResolver.resolveViewName(response.getViewName());
        }
        MimeType mimeType = MimeType.from(view.getName());
        response.setContentType(mimeType);
        response.setBody(Files.readString(view.toPath(), StandardCharsets.UTF_8));
    }

    private Handler getHandler(Request request) {
        return requestMappingInfos.stream()
                .map(requestMappingInfo -> requestMappingInfo.getHandler(request))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseGet(ResourceHandler::getInstance);
    }
}
