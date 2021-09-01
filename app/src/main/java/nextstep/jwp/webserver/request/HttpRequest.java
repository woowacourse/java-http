package nextstep.jwp.webserver.request;

import java.util.Map;
import nextstep.jwp.webserver.response.HttpResponse;

public interface HttpRequest {

    HttpMethod httpMethod();

    String httpUrl();

    String getAttribute(String key);

    Map<String, String> getRequestParams();

    HttpCookie getCookie();

    HttpSession getSession();

    void addSessionCreator(SessionUtil sessionUtil);
}
