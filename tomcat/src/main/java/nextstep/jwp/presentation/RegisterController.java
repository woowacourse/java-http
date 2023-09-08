package nextstep.jwp.presentation;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.RequestReader;
import org.apache.coyote.http11.response.Response;

import java.io.IOException;

import static org.apache.coyote.http11.response.Header.LOCATION;
import static org.apache.coyote.http11.Method.GET;
import static org.apache.coyote.http11.Method.POST;
import static org.apache.coyote.http11.response.StatusCode.FOUND;
import static org.apache.coyote.http11.response.StatusCode.OK;

public class RegisterController implements Controller {

    private static final String INDEX = "/index.html";

    @Override
    public Response service(RequestReader requestReader) throws IOException {
        String method = requestReader.getMethod();

        if (GET.matches(method)) {
            return registerPage(requestReader);
        }
        if (POST.matches(method)) {
            return register(requestReader);
        }

        return null;
    }

    private Response registerPage(RequestReader requestReader) throws IOException {
        return new Response(OK)
                .addBaseHeader(requestReader.getContentType())
                .createBodyByFile(requestReader.getUri());
    }

    private Response register(RequestReader requestReader) throws IOException {
        InMemoryUserRepository.save(new User(
                requestReader.getBodyValue("account"),
                requestReader.getBodyValue("email"),
                requestReader.getBodyValue("password")
        ));
        return new Response(FOUND)
                .createBodyByFile(INDEX)
                .addHeader(LOCATION, INDEX)
                .addBaseHeader(requestReader.getContentType());
    }
}
