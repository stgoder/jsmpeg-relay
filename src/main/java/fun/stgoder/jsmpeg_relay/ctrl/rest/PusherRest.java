package fun.stgoder.jsmpeg_relay.ctrl.rest;

import fun.stgoder.jsmpeg_relay.common.Code;
import fun.stgoder.jsmpeg_relay.common.exception.BLException;
import fun.stgoder.jsmpeg_relay.common.exception.ExecException;
import fun.stgoder.jsmpeg_relay.common.model.Resp;
import fun.stgoder.jsmpeg_relay.ps.model.PusherB;
import fun.stgoder.jsmpeg_relay.ps.pusher.Pusher;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rest/pusher")
public class PusherRest {
    @GetMapping
    public Resp pusher() {
        List<PusherB> pushers = new ArrayList<>();
        for (Pusher pusher : Pusher.pushers()) {
            pushers.add(PusherB.fromPusher(pusher));
        }
        return new Resp(Code.REQUEST_OK, pushers);
    }

    @PostMapping("/startAndPut")
    public Resp startAndPut(@RequestParam("streamId") String streamId,
                            @RequestParam("source") String source,
                            @RequestParam("keepAlive") boolean keepAlive,
                            @RequestParam(value = "cancelAfterSeconds", required = false, defaultValue = "0")
                                    long cancelAfterSeconds) throws BLException, ExecException {
        if (StringUtils.isBlank(streamId))
            throw new BLException(-1, "streamId blank");
        if (StringUtils.isBlank(source))
            throw new BLException(-1, "source blank");
        Pusher.startAndPut(streamId, source, keepAlive, cancelAfterSeconds);
        return new Resp(Code.REQUEST_OK);
    }

    @PostMapping("/stopAndRemove")
    public Resp stopAndRemove(@RequestParam("streamId") String streamId) throws ExecException, BLException {
        Pusher.stopAndRemove(streamId);
        return new Resp(Code.REQUEST_OK);
    }
}
