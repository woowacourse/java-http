package com.techcourse.infrastructure;

import com.techcourse.presentation.DefaultPresentation;
import com.techcourse.presentation.LoginPresentation;
import com.techcourse.presentation.RootPresentation;
import java.util.List;
import org.apache.coyote.ioprocessor.parser.HttpRequest;
import org.apache.coyote.ioprocessor.parser.HttpResponse;

public class PresentationResolver {

    private static final DefaultPresentation defaultPresentation = new DefaultPresentation();

    List<Presentation> presentations;

    public PresentationResolver(List<Presentation> presentations) {
        this.presentations = presentations;
    }

    public PresentationResolver() {
        this(List.of(
                new LoginPresentation(),
                new RootPresentation())
        );
    }

    public HttpResponse resolve(HttpRequest request) {
        return presentations.stream()
                .filter(presentation -> presentation.match(request))
                .findFirst()
                .orElse(defaultPresentation)
                .view(request);
    }
}
