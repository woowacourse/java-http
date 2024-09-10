package hoony.was;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StaticResourceLoader;

public class StaticResourceReturnValueResolver implements ReturnValueResolver {

    private static final String REDIRECT_PREFIX = "redirect:";
    private static final String EXTENSION_SEPARATOR = ".";

    @Override
    public boolean supportsReturnType(Class<?> returnType) {
        return returnType.equals(String.class);
    }

    @Override
    public HttpResponse resolve(Object returnValue) {
        String path = String.valueOf(returnValue);
        if (path.startsWith(REDIRECT_PREFIX)) {
            return HttpResponse.builder()
                    .found(path.substring(REDIRECT_PREFIX.length()))
                    .build();
        }
        byte[] resource = StaticResourceLoader.load(path);
        String extension = path.substring(path.lastIndexOf(EXTENSION_SEPARATOR) + 1);
        String responseBody = new String(resource);
        return HttpResponse.builder()
                .ok()
                .contentType(ContentType.fromExtension(extension))
                .body(responseBody)
                .build();
    }
}
