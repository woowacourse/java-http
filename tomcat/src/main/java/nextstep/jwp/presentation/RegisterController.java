package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpDataRequest;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseHeaders;
import org.apache.coyote.http11.response.StatusLine;
import org.apache.coyote.http11.utils.IOUtils;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String resource = IOUtils.readResourceFile(request.getPath());
        response.setStatusLine(new StatusLine(HttpStatus.OK))
                .setHeaders(ResponseHeaders.create(request, resource))
                .setResource(resource);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        HttpDataRequest joinData = HttpDataRequest.extractRequest(request.getRequestBody());
        User user = new User(joinData.get("account"), joinData.get("password"), joinData.get("email"));
        InMemoryUserRepository.save(user);
        log.info("save user: {}", user);
        String resource = IOUtils.readResourceFile("/index.html");
        response.setStatusLine(new StatusLine(HttpStatus.FOUND))
                .setHeaders(ResponseHeaders.create(request, resource))
                .setResource(resource);
    }
}
