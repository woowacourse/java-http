package hoony.was;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.HttpResponse;

public class ReturnValueResolverMapper {

    private final List<ReturnValueResolver> returnValueResolvers;

    public ReturnValueResolverMapper() {
        this.returnValueResolvers = new ArrayList<>();
    }

    public void register(ReturnValueResolver returnValueResolver) {
        returnValueResolvers.add(returnValueResolver);
    }

    public HttpResponse resolve(Object returnValue) {
        return returnValueResolvers.stream()
                .filter(resolver -> resolver.supportsReturnType(returnValue.getClass()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No ReturnValueResolver found for returnType"))
                .resolve(returnValue);
    }
}
