package org.apache.coyote.http11.config;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.coyote.http11.resolver.RequestResolver;
import org.apache.coyote.http11.resolver.ServletResolver;
import org.apache.coyote.http11.resolver.StaticResourceResolver;

public class TomcatServerConfiguration {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final String DEFAULT_CHARSET_NAME = DEFAULT_CHARSET.name().toLowerCase();

    public static final String STATIC_RESOURCE_PATH = "static";
    public static final List<RequestResolver> requestResolvers = List.of(
            new StaticResourceResolver(),
            new ServletResolver()
    );


}
