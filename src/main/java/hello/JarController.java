package hello;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class JarController {

    @RequestMapping({"/jar"})
    public String jar() {
        return "jar";
    }
}
