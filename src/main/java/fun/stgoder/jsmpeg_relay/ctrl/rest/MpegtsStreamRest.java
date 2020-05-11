package fun.stgoder.jsmpeg_relay.ctrl.rest;

import fun.stgoder.jsmpeg_relay.common.model.Resp;
import fun.stgoder.jsmpeg_relay.server.model.MpegtsStream;
import fun.stgoder.jsmpeg_relay.server.mpegts.MpegtsGroup;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/mpegts-stream")
public class MpegtsStreamRest {
    @GetMapping
    public Resp player() {
        List<MpegtsStream> streams = MpegtsGroup.list();
        return Resp.ok(streams);
    }

    @PostMapping("/closeAndRemove")
    public Resp closeAndRemove(@RequestParam("channelId") String channelId) {
        MpegtsGroup.closeAndRemove(channelId);
        return Resp.ok();
    }
}
