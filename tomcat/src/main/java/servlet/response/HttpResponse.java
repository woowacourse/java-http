package servlet.response;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.util.HeaderDto;
import org.apache.coyote.http.util.HttpHeaderConsts;

public class HttpResponse {

    private final List<HeaderDto> headerDtos = new ArrayList<>();
    private final List<HttpCookie> cookies = new ArrayList<>();

    public void sendRedirect(final String redirectPath) {
        headerDtos.add(new HeaderDto(HttpHeaderConsts.LOCATION, redirectPath));
    }

    public void addCookie(final HttpCookie cookie) {
        cookies.add(cookie);
    }
}
