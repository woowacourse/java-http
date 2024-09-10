package hoony.was;

import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StaticResourceLoader;

public class StaticResourceReturnValueResolver implements ReturnValueResolver {

    @Override
    public boolean supportsReturnType(Class<?> returnType) {
        return returnType.equals(String.class);
    }

    @Override
    public HttpResponse resolve(Object returnValue) {
        String path = String.valueOf(returnValue);
        byte[] resource = StaticResourceLoader.load(path);
        String extension = path.substring(path.lastIndexOf(".") + 1);
        String responseBody = new String(resource);
        return HttpResponse.builder()
                .ok()
                .contentType(ContentType.fromExtension(extension))
                .body(responseBody)
                .build();
    }
}
