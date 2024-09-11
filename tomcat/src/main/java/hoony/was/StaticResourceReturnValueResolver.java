package hoony.was;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StaticResourceLoader;
import org.apache.coyote.http11.StatusCode;

public class StaticResourceReturnValueResolver implements ReturnValueResolver {

    private static final String REDIRECT_PREFIX = "redirect:";
    private static final String EXTENSION_SEPARATOR = ".";

    @Override
    public boolean supportsReturnType(Class<?> returnType) {
        return returnType.equals(String.class);
    }

    @Override
    public void resolve(HttpRequest request, HttpResponse response, Object returnValue) {
        String path = String.valueOf(returnValue);
        if (path.startsWith(REDIRECT_PREFIX)) {
            response.setStatusCode(StatusCode.FOUND);
            response.setLocation(path.substring(REDIRECT_PREFIX.length()));
            return;
        }
        byte[] resource = StaticResourceLoader.load(path);
        String extension = path.substring(path.lastIndexOf(EXTENSION_SEPARATOR) + 1);
        String responseBody = new String(resource);
        response.setStatusCode(StatusCode.OK);
        response.setContentType(ContentType.fromExtension(extension));
        response.setBody(responseBody);
    }
}
