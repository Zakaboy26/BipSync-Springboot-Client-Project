package uk.ac.cf.cm6213.group4.uk.ac.cf.cm6213.group4.Login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class HomeController {
    @GetMapping("/")
    public RedirectView home() {
        return new RedirectView("/list");
    }
}
