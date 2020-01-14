package fun.stgoder.jsmpeg_relay.ctrl.view;

import fun.stgoder.jsmpeg_relay.server.model.PlayerStream;
import fun.stgoder.jsmpeg_relay.server.relay.PlayerGroups;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/player-stream")
public class PlayerStreamView {
    @GetMapping({"/", ""})
    public ModelAndView player(ModelAndView mv) {
        mv.setViewName("player-stream");
        mv.addObject("title", "player-stream");
        mv.addObject("tab", "player-stream");
        List<PlayerStream> streams = PlayerGroups.list();
        mv.addObject("streams", streams);
        return mv;
    }

    @PostMapping("/closeAndRemove")
    public ModelAndView closeAndRemove(@RequestParam("channelId") String channelId, ModelAndView mv) {
        mv.setViewName("redirect:/player-stream/");
        PlayerGroups.closeAndRemove(channelId);
        return mv;
    }
}
