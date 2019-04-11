package hello;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BinController {

    @RequestMapping({"/bin"})
    public String jar() {
        return "bin";
    }
}

