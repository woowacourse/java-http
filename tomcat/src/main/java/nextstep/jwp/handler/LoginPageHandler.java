package nextstep.jwp.handler;

import java.io.IOException;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.HttpStatus;
import org.apache.coyote.http.SupportFile;
import org.apache.coyote.http.vo.HttpHeaders;
import org.apache.coyote.http.vo.HttpRequest;
import org.apache.coyote.http.vo.HttpResponse;
import org.apache.coyote.http.vo.Url;
import util.ResourceFileReader;

public class LoginPageHandler implements Handler {

    @Override
    public HttpResponse handle(final HttpRequest request) {
        try{
            final HttpHeaders headers = HttpHeaders.getEmptyHeaders();
            headers.put(HttpHeader.CONTENT_TYPE, SupportFile.HTML.getContentType());
            final String body = ResourceFileReader.readFile("/login.html");

            return new HttpResponse(HttpStatus.OK, headers, body);
        }catch (IOException e){
            throw new UncheckedServletException(e);
        }
    }

    @Override
    public boolean isSupported(final HttpRequest request) {
        return request.isRequestMethodOf(HttpMethod.GET) && request.isUrl(Url.from("/login"));
    }
}
