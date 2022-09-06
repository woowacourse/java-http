package org.apache.coyote.http11.http11request;

import java.util.List;
import org.apache.coyote.http11.dto.ResponseComponent;
import org.apache.coyote.http11.http11handler.exception.NoProperHandlerException;
import org.apache.coyote.http11.http11response.Http11ResponseHandler;
import org.apache.coyote.http11.http11response.RedirectResponseHandler;
import org.apache.coyote.http11.http11response.ResourceResponseHandler;

public class Http11ResponseHandlerSelector {

    private List<Http11ResponseHandler> http11ResponseHandlers = List.of(
            new RedirectResponseHandler(), new ResourceResponseHandler()
    );

    public Http11ResponseHandler getHttp11ResponseHandler(ResponseComponent responseComponent) {
        return http11ResponseHandlers.stream()
                .filter(it -> it.isProper(responseComponent))
                .findAny()
                .orElseThrow(NoProperHandlerException::new);
    }
}
