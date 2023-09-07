package nextstep.jwp.servlets;

import static org.apache.coyote.http11.PagePathMapper.INDEX_PAGE;
import static org.apache.coyote.http11.PagePathMapper.REGISTER_PAGE;
import static org.apache.coyote.http11.message.HttpHeaders.*;

import java.io.IOException;
import java.util.Map;
import nextstep.jwp.database.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.RequestBody;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.ResponseBody;

public class RegisterServlet extends Servlet {

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if (httpRequest.getMethod().isEqualTo(HttpMethod.GET)) {
            doGet(httpRequest, httpResponse);
        }
        if (httpRequest.getMethod().isEqualTo(HttpMethod.POST)) {
            doPost(httpRequest, httpResponse);
        }
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String absolutePath = REGISTER_PAGE.path();

        String content = findResourceWithPath(absolutePath);
        ResponseBody responseBody = new ResponseBody(content);

        httpResponse.setHttpVersion(httpRequest.getHttpVersion())
                .setHttpStatus(HttpStatus.OK)
                .addHeader(CONTENT_TYPE, ContentType.parse(absolutePath))
                .addHeader(CONTENT_LENGTH, String.valueOf(content.getBytes().length))
                .setResponseBody(responseBody);
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        saveUser(httpRequest);
        String absolutePath = INDEX_PAGE.path();

        httpResponse.setHttpVersion(httpRequest.getHttpVersion())
                .setHttpStatus(HttpStatus.FOUND)
                .addHeader(LOCATION, absolutePath)
                .setResponseBody(ResponseBody.ofEmpty());
    }

    private void saveUser(HttpRequest httpRequest) {
        RequestBody requestBody = httpRequest.getBody();
        Map<String, String> formData = requestBody.getAsFormData();

        String account = formData.get("account");
        String password = formData.get("password");
        String email = formData.get("email");
        User user = new User(account, password, email);

        InMemoryUserRepository.save(user);
    }
}
