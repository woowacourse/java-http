package cache.com.example.version;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VersionHandlebarsHelper implements Helper<Object> {

    private static final Logger log = LoggerFactory.getLogger(VersionHandlebarsHelper.class);

    private final ResourceVersion version;

    @Autowired
    public VersionHandlebarsHelper(ResourceVersion version) {
        this.version = version;
    }

    public String staticUrls(String path, Options options) {
        log.debug("static url : {}", path);
        return String.format("/resources/%s%s", version.getVersion(), path);
    }

    @Override
    public Object apply(Object context, Options options) {
        return staticUrls(context.toString(), options);
    }
}
