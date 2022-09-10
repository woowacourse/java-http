package nextstep.jwp.presentation;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StatusCode;

public class RegisterController extends AbstractController {

    @Override
    protected HttpResponse doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final User user = new User(httpRequest.getBodyValue("account"), httpRequest.getBodyValue("password"),
                httpRequest.getBodyValue("email"));
        InMemoryUserRepository.save(user);

        return redirect(httpRequest, httpResponse);
    }

    private HttpResponse redirect(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final HttpBody httpBody = HttpBody.createByUrl(REDIRECT_URL);
        final HttpHeader httpHeader = defaultHeader(StatusCode.MOVED_TEMPORARILY, httpBody, httpRequest.getUrl());
        httpHeader.location(REDIRECT_URL);
        return httpResponse.header(httpHeader).body(httpBody);
    }

    @Override
    protected HttpResponse doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        final HttpBody httpBody = HttpBody.createByUrl(httpRequest.getUrl());

        final HttpHeader httpHeader = new HttpHeader().startLine(StatusCode.OK)
                .contentType(httpRequest.getUrl())
                .contentLength(httpBody.getBody().getBytes().length);

        return httpResponse.header(httpHeader).body(httpBody);
    }
}
