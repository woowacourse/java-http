package thread.stage2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.atomic.AtomicInteger;

@Controller
public class SampleController {

    private static final Logger log = LoggerFactory.getLogger(SampleController.class);

    private static final AtomicInteger count = new AtomicInteger(0);

    private final HelloWorldService helloWorldService;

    @Autowired
    public SampleController(final HelloWorldService helloWorldService) {
        this.helloWorldService = helloWorldService;
    }

    @GetMapping("/test")
    @ResponseBody
    public String helloWorld() throws InterruptedException {
        Thread.sleep(500);
        log.info("http call count : {}", count.incrementAndGet());
        return helloWorldService.helloWorld();
    }
}
