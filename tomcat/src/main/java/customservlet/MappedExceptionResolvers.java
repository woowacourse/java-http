package customservlet;

import java.util.Map;
import java.util.Map.Entry;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;

public class MappedExceptionResolvers {

    private final Map<Class<? extends RuntimeException>, ExceptionResolver> exceptionResolvers;

    private MappedExceptionResolvers(
            final Map<Class<? extends RuntimeException>, ExceptionResolver> exceptionResolvers) {
        this.exceptionResolvers = exceptionResolvers;
    }

    public static MappedExceptionResolvers from(
            final Map<Class<? extends RuntimeException>, ExceptionResolver> exceptionResolvers) {
        return new MappedExceptionResolvers(exceptionResolvers);
    }

    public void resolveException(final RuntimeException exception, final HttpRequest request,
                                 final HttpResponse response) {
        getResolver(exception).resolveException(request, response);
    }

    private ExceptionResolver getResolver(final Exception exception) {
        return exceptionResolvers.entrySet()
                .stream()
                .filter(it -> it.getKey().isInstance(exception))
                .map(Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(exception.getClass().getName() + "를 처리할 Resolver가 존재하지 않습니다."));
    }
}
