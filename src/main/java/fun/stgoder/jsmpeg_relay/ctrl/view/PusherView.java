package fun.stgoder.jsmpeg_relay.ctrl.view;

import fun.stgoder.jsmpeg_relay.common.Constants;
import fun.stgoder.jsmpeg_relay.common.exception.BLException;
import fun.stgoder.jsmpeg_relay.common.exception.ExecException;
import fun.stgoder.jsmpeg_relay.ps.model.PusherB;
import fun.stgoder.jsmpeg_relay.ps.pusher.Pusher;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/pusher")
public class PusherView {
    @GetMapping({"/", ""})
    public ModelAndView pusher(ModelAndView mv) {
        mv.setViewName("pusher");
        mv.addObject("title", "pusher");
        mv.addObject("tab", "pusher");
        List<PusherB> pushers = new ArrayList<>();
        for (Pusher pusher : Pusher.pushers()) {
            pushers.add(PusherB.fromPusher(pusher));
        }
        mv.addObject("pushers", pushers);
        return mv;
    }

    @PostMapping("/startAndPut")
    public ModelAndView startAndPut(@RequestParam("streamId") String streamId,
                                    @RequestParam("source") String source,
                                    @RequestParam("keepAlive") boolean keepAlive,
                                    @RequestParam(value = "cancelAfterSeconds", required = false, defaultValue = "0")
                                            long cancelAfterSeconds, ModelAndView mv) throws BLException, ExecException {
        mv.setViewName("redirect:/pusher/");
        if (StringUtils.isBlank(streamId))
            throw new BLException(-1, "streamId blank");
        if (StringUtils.isBlank(source))
            throw new BLException(-1, "source blank");
        Pusher.startAndPut(streamId, source, keepAlive, cancelAfterSeconds);
        return mv;
    }

    @PostMapping("/stopAndRemove")
    public ModelAndView stopAndRemove(@RequestParam("streamId") String streamId,
                                      ModelAndView mv) throws ExecException, BLException {
        mv.setViewName("redirect:/pusher/");
        Pusher.stopAndRemove(streamId);
        return mv;
    }

    @GetMapping("/play-jsmpeg/{streamId}")
    public ModelAndView play(@PathVariable("streamId") String streamId, ModelAndView mv) throws Exception {
        mv.setViewName("play-jsmpeg");
        mv.addObject("url", "ws://" + Constants.localIpv4 + ":"
                + Constants.RELAY_SERVER_PORT + "?streamId=" + streamId);
        return mv;
    }
}
