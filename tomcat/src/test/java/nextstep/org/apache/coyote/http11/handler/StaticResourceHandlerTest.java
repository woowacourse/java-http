package nextstep.org.apache.coyote.http11.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.apache.coyote.http11.handler.StaticResourceHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StaticResourceHandlerTest {

    @DisplayName("경로에 해당하는 파일을 읽는다")
    @Test
    void readFile() throws IOException {
        final String path = "/401.html";
        final String expected = "<!DOCTYPE html>\n"
                + "<html lang=\"en\">\n"
                + "    <head>\n"
                + "        <meta charset=\"utf-8\" />\n"
                + "        <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n"
                + "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\" />\n"
                + "        <meta name=\"description\" content=\"\" />\n"
                + "        <meta name=\"author\" content=\"\" />\n"
                + "        <title>404 Error - SB Admin</title>\n"
                + "        <link href=\"css/styles.css\" rel=\"stylesheet\" />\n"
                + "        <script src=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/js/all.min.js\" crossorigin=\"anonymous\"></script>\n"
                + "    </head>\n"
                + "    <body>\n"
                + "        <div id=\"layoutError\">\n"
                + "            <div id=\"layoutError_content\">\n"
                + "                <main>\n"
                + "                    <div class=\"container\">\n"
                + "                        <div class=\"row justify-content-center\">\n"
                + "                            <div class=\"col-lg-6\">\n"
                + "                                <div class=\"text-center mt-4\">\n"
                + "                                    <h1 class=\"display-1\">401</h1>\n"
                + "                                    <p class=\"lead\">Unauthorized</p>\n"
                + "                                    <p>Access to this resource is denied.</p>\n"
                + "                                    <a href=\"index.html\">\n"
                + "                                        <i class=\"fas fa-arrow-left me-1\"></i>\n"
                + "                                        Return to Dashboard\n"
                + "                                    </a>\n"
                + "                                </div>\n"
                + "                            </div>\n"
                + "                        </div>\n"
                + "                    </div>\n"
                + "                </main>\n"
                + "            </div>\n"
                + "            <div id=\"layoutError_footer\">\n"
                + "                <footer class=\"py-4 bg-light mt-auto\">\n"
                + "                    <div class=\"container-fluid px-4\">\n"
                + "                        <div class=\"d-flex align-items-center justify-content-between small\">\n"
                + "                            <div class=\"text-muted\">Copyright &copy; Your Website 2021</div>\n"
                + "                            <div>\n"
                + "                                <a href=\"#\">Privacy Policy</a>\n"
                + "                                &middot;\n"
                + "                                <a href=\"#\">Terms &amp; Conditions</a>\n"
                + "                            </div>\n"
                + "                        </div>\n"
                + "                    </div>\n"
                + "                </footer>\n"
                + "            </div>\n"
                + "        </div>\n"
                + "        <script src=\"https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js\" crossorigin=\"anonymous\"></script>\n"
                + "        <script src=\"js/scripts.js\"></script>\n"
                + "    </body>\n"
                + "</html>\n";

        final String actual = StaticResourceHandler.readFile(path);

        assertThat(actual).isEqualTo(expected);
    }
}
