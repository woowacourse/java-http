package org.apache.catalina;

import org.apache.coyote.Controller;
import org.apache.coyote.http11.URL;

public interface RequestMapping {

    Controller map(final URL url);
}
