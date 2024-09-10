package hoony.was;

import org.apache.coyote.http11.HttpResponse;

public interface ReturnValueResolver {

    boolean supportsReturnType(Class<?> returnType);

    HttpResponse resolve(Object returnValue);
}
