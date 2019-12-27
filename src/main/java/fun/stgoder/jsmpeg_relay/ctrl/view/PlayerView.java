package fun.stgoder.jsmpeg_relay.ctrl.view;

import fun.stgoder.jsmpeg_relay.server.model.StreamB;
import fun.stgoder.jsmpeg_relay.server.relay.PlayerGroups;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/player")
public class PlayerView {
    @GetMapping({"/", ""})
    public ModelAndView player(ModelAndView mv) {
        mv.setViewName("player");
        mv.addObject("title", "player");
        mv.addObject("tab", "player");
        List<StreamB> streams = PlayerGroups.list();
        mv.addObject("streams", streams);
        return mv;
    }

    @PostMapping("/closeAndRemove")
    public ModelAndView closeAndRemove(@RequestParam("channelId") String channelId, ModelAndView mv) {
        mv.setViewName("redirect:/player/");
        PlayerGroups.closeAndRemove(channelId);
        return mv;
    }
}
