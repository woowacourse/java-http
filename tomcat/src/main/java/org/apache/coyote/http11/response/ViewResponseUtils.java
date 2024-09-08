package org.apache.coyote.http11.response;

import java.util.Map;
import org.apache.coyote.http11.response.view.View;
import org.apache.coyote.http11.response.view.ViewType;

public class ViewResponseUtils {

    public static HttpResponse createResponse(View view) {
        return HttpResponse.builder()
                .status(view.getStatus())
                .addHeaders(view.getAddedHeaders())
                .contentType(view.getContentType())
                .body(view.getResponseBody())
                .build();
    }
}
