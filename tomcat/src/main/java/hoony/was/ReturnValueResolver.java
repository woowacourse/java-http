package hoony.was;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public interface ReturnValueResolver {

    boolean supportsReturnType(Class<?> returnType);

    void resolve(HttpRequest request, HttpResponse response, Object returnValue);
}
