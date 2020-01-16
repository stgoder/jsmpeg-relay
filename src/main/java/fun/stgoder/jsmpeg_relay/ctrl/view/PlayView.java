package fun.stgoder.jsmpeg_relay.ctrl.view;

import fun.stgoder.jsmpeg_relay.common.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/play")
public class PlayView {
    @GetMapping("/jsmpeg/{streamId}")
    public ModelAndView play(@PathVariable("streamId") String streamId, ModelAndView mv) throws Exception {
        mv.setViewName("play-jsmpeg");
        mv.addObject("url", "ws://" + Constants.localIpv4 + ":"
                + Constants.RELAY_SERVER_PORT + "?streamId=" + streamId);
        return mv;
    }
}
