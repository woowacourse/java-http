package cache.com.example.version;

import com.github.jknack.handlebars.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import pl.allegro.tech.boot.autoconfigure.handlebars.HandlebarsHelper;

import static cache.com.example.version.CacheBustingWebConfig.PREFIX_STATIC_RESOURCES;

@HandlebarsHelper
public class VersionHandlebarsHelper {

    private static final Logger log = LoggerFactory.getLogger(VersionHandlebarsHelper.class);

    private final ResourceVersion version;

    @Autowired
    public VersionHandlebarsHelper(ResourceVersion version) {
        this.version = version;
    }

    public String staticUrls(String path, Options options) {
        log.debug("static url : {}", path);
        return String.format(PREFIX_STATIC_RESOURCES + "/%s%s", version.getVersion(), path);
    }
}
