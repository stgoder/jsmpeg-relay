package fun.stgoder.jsmpeg_relay.ctrl.view;

import fun.stgoder.jsmpeg_relay.server.model.MpegtsStream;
import fun.stgoder.jsmpeg_relay.server.mpegts.MpegtsGroup;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/mpegts-stream")
public class MpegtsStreamView {
    @GetMapping({"/", ""})
    public ModelAndView player(ModelAndView mv) {
        mv.setViewName("mpegts-stream");
        mv.addObject("title", "mpegts-stream");
        mv.addObject("tab", "mpegts-stream");
        List<MpegtsStream> streams = MpegtsGroup.list();
        mv.addObject("streams", streams);
        return mv;
    }

    @PostMapping("/closeAndRemove")
    public ModelAndView closeAndRemove(@RequestParam("channelId") String channelId, ModelAndView mv) {
        mv.setViewName("redirect:/mpegts-stream/");
        MpegtsGroup.closeAndRemove(channelId);
        return mv;
    }
}
