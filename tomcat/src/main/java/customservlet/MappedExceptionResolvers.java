package customservlet;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;

public class MappedExceptionResolvers {

    private static final MappedExceptionResolvers mappedExceptionResolvers = new MappedExceptionResolvers();
    private static final Map<Class<? extends RuntimeException>, ExceptionResolver> exceptionResolvers = new HashMap<>();

    private MappedExceptionResolvers() {
    }

    public static MappedExceptionResolvers getInstance() {
        return mappedExceptionResolvers;
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

    public void addResolver(final Class<? extends RuntimeException> exceptionClass,
                            final ExceptionResolver exceptionResolver) {
        exceptionResolvers.put(exceptionClass, exceptionResolver);
    }
}
