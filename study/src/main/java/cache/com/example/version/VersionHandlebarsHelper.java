package cache.com.example.version;

import com.github.jknack.handlebars.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.allegro.tech.boot.autoconfigure.handlebars.HandlebarsHelper;

@HandlebarsHelper
public class VersionHandlebarsHelper {

    private static final Logger log = LoggerFactory.getLogger(VersionHandlebarsHelper.class);

    private final ResourceVersion version;

    public VersionHandlebarsHelper(ResourceVersion version) {
        this.version = version;
    }

    public String staticUrls(String path, Options options) {
        log.debug("static url : {}", path);
        return String.format("/resources/%s%s", version.getVersion(), path);
    }
}
