package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.http11.request.Extension;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.Path;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.support.ResourceFindUtils;

public class RegisterController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) {
        final Path path = request.getPath();
        if (request.isGetMethod()) {
            final String responseBody = ResourceFindUtils
                    .getResourceFile(path.getResource() + Extension.HTML.getExtension());
            return new HttpResponse.Builder()
                    .status(HttpStatus.OK)
                    .contentType(path.getContentType())
                    .responseBody(responseBody)
                    .build();
        }

        throw new UncheckedServletException("지원하지 않는 메서드입니다.");
    }
}
