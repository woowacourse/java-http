package org.apache.coyote.view;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;
import org.apache.coyote.response.ResponseBody;
import org.apache.coyote.response.ResponseHeader;

public class ViewResolver {

    public Response resolve(Request request, ViewResource viewResource) {

        try {
            ResponseBody responseBody = new ResponseBody(new String(Files.readAllBytes(viewResource.getPath())));

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", request.getResourceTypes() + ";charset=utf-8");
            headers.put("Content-Length", responseBody.getLength());
            ResponseHeader responseHeader = new ResponseHeader(request.getProtocol(), viewResource.getHttpStatus(), headers);
            return new Response(responseHeader, responseBody);
        } catch (IOException e) {
            throw new IllegalArgumentException("파일 경로가 잘못되었습니다.");
        }
    }
}
