package nextstep.jwp.http.response;

import java.io.IOException;
import nextstep.jwp.util.HttpStatus;
import nextstep.jwp.util.StaticResources;
import nextstep.jwp.util.ViewResolver;

public class StaticResponse {

    private final StaticResources staticResource;
    private final String path;

    public StaticResponse(StaticResources staticResource, String path) {
        this.staticResource = staticResource;
        this.path = path;
    }

    public String response() throws IOException {
        final ViewResolver viewResolver = new ViewResolver(path);
        final String responseBody = viewResolver.staticValue(staticResource.resource());
        String firstLine = String.join(" ","HTTP/1.1", HttpStatus.OK.value(),
            HttpStatus.OK.method());
        return String.join("\r\n",
            firstLine,
            "Content-Type:" + staticResource.type(),
            "Content-Length: " + responseBody.getBytes().length + " ",
            "",
            responseBody);
    }
}
