package cache.com.example.version;

import com.github.jknack.handlebars.springmvc.HandlebarsViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;

@Configuration
public class HandlebarsConfig {

    private final VersionHandlebarsHelper versionHandlebarsHelper;

    public HandlebarsConfig(VersionHandlebarsHelper versionHandlebarsHelper) {
        this.versionHandlebarsHelper = versionHandlebarsHelper;
    }

    @Bean
    public ViewResolver handlebarsViewResolver() {
        HandlebarsViewResolver viewResolver = new HandlebarsViewResolver();
        viewResolver.registerHelper("staticUrls", versionHandlebarsHelper);
        viewResolver.setPrefix("classpath:/templates/");
        viewResolver.setSuffix(".html");

        return viewResolver;
    }
}