package nextstep.jwp.controller;

import nextstep.jwp.http.SupportedContentType;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.ResponseBody;
import nextstep.jwp.http.response.ResponseHeader;
import nextstep.jwp.http.response.ResponseLine;
import nextstep.jwp.http.response.ResponseReference;
import nextstep.jwp.http.response.ResponseStatus;

public class ResourceController extends AbstractController {
    @Override
    protected HttpResponse doGet(HttpRequest request) throws Exception {
        SupportedContentType contentType = SupportedContentType.extractContentTypeFromRequest(request);
        if (contentType == SupportedContentType.NOTFOUND) {
            return ResponseReference.create404Response();
        }

        try {
            String responseBody = ResponseBody.createStaticFileByFileName(request.getUri()).getResponseBody();
            ResponseStatus responseStatus = ResponseStatus.OK;
            return new HttpResponse(
                    new ResponseLine(responseStatus.getStatusCode(), responseStatus.getStatusMessage()),
                    new ResponseHeader.ResponseHeaderBuilder(contentType,
                            responseBody.getBytes().length).build(),
                    ResponseBody.createByString(responseBody));
        } catch (Exception e) {
            return ResponseReference.create404Response();
        }

    }
}
