package fun.stgoder.jsmpeg_relay.ctrl.rest;

import fun.stgoder.jsmpeg_relay.common.Code;
import fun.stgoder.jsmpeg_relay.common.model.Resp;
import fun.stgoder.jsmpeg_relay.server.model.StreamB;
import fun.stgoder.jsmpeg_relay.server.relay.PlayerGroups;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/player")
public class PlayerRest {
    @GetMapping
    public Resp player() {
        List<StreamB> streams = PlayerGroups.list();
        return new Resp(Code.REQUEST_OK, streams);
    }

    @PostMapping("/closeAndRemove")
    public Resp closeAndRemove(@RequestParam("channelId") String channelId) {
        PlayerGroups.closeAndRemove(channelId);
        return new Resp(Code.REQUEST_OK);
    }
}
