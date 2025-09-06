package org.apache.coyote.http11;

import java.util.Map;

public record ResourceResolutionResult(
    String resource,
    Map<String, String> queryParams,
    boolean isRedirect,
    boolean isUnauthorized
) {

    public static ResourceResolutionResult of(String resource) {
        return new ResourceResolutionResult(resource, null, false, false);
    }

    public static ResourceResolutionResult of(String resource, Map<String, String> queryParams) {
        return new ResourceResolutionResult(resource, queryParams, false, false);
    }
    
    public static ResourceResolutionResult redirect(String location) {
        return new ResourceResolutionResult(location, null, true, false);
    }
    
    public static ResourceResolutionResult unauthorized(String errorPage) {
        return new ResourceResolutionResult(errorPage, null, false, true);
    }

    public boolean hasAuthParam() {
        return queryParams != null && queryParams.containsKey("account") && queryParams.containsKey("password");
    }
}
