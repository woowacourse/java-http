package org.apache.coyote.http11.response;

import java.util.Map;
import org.apache.coyote.http11.response.view.View;
import org.apache.coyote.http11.response.view.ViewType;

public class ViewResponseUtils {
    private static final Map<ViewType, String> VIEW_TYPE_TO_CONTENT_TYPE = Map.of(ViewType.HTML, "text/html");

    public static String createResponse(View view) {
        String responseBody = view.getContent();
        String contentType = VIEW_TYPE_TO_CONTENT_TYPE.get(view.getType());

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }
}
