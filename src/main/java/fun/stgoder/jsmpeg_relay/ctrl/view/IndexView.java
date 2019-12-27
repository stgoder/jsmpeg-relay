package fun.stgoder.jsmpeg_relay.ctrl.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class IndexView {
    @GetMapping({"/", ""})
    public ModelAndView index(ModelAndView mv) {
        mv.setViewName("index");
        mv.addObject("title", "jsmpeg-relay");
        mv.addObject("tab", "index");
        return mv;
    }
}
