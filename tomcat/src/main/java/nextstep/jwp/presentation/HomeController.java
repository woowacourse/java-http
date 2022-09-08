package nextstep.jwp.presentation;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseHeaders;
import org.apache.coyote.http11.response.StatusLine;
import org.apache.coyote.http11.utils.IOUtils;

public class HomeController extends AbstractController {
    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String resource = IOUtils.readResourceFile(request.getPath());
        response.setStatusLine(new StatusLine(HttpStatus.OK))
                .setHeaders(ResponseHeaders.create(request, resource))
                .setResource(resource);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        throw new UnsupportedOperationException("HomePage에는 POST요청이 들어올 수 없습니다.");
    }
}
