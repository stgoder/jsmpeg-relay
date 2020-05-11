package fun.stgoder.jsmpeg_relay.ctrl.rest;

import fun.stgoder.jsmpeg_relay.common.model.Resp;
import fun.stgoder.jsmpeg_relay.server.model.PlayerStream;
import fun.stgoder.jsmpeg_relay.server.relay.PlayerGroups;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/player-stream")
public class PlayerStreamRest {
    @GetMapping
    public Resp player() {
        List<PlayerStream> streams = PlayerGroups.list();
        return Resp.ok(streams);
    }

    @PostMapping("/closeAndRemove")
    public Resp closeAndRemove(@RequestParam("channelId") String channelId) {
        PlayerGroups.closeAndRemove(channelId);
        return Resp.ok();
    }
}
