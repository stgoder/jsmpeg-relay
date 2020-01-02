package fun.stgoder.jsmpeg_relay.common;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.Map;

@SuppressWarnings(value = {"rawtypes", "unchecked"})
public class Constants {
    public static final int SERVER_PORT;
    public static final String SERVER_SERVLET_CONTEXT_PATH;

    public static final boolean TEST_MODE;
    public static final String localIpv4;
    public static final String FFMPEG_PATH;
    public static final String PSLOG_PATH;

    public static final int RELAY_SERVER_PORT;
    public static final int MPEGTS_SERVER_PORT;

    static {
        Yaml yaml = new Yaml();
        Map base = yaml.loadAs(Constants.class.getResourceAsStream("/application.yml"), Map.class);
        Map server = (Map) base.get("server");
        SERVER_PORT = (int) server.getOrDefault("port", "10019");
        Map server_servlet = (Map) server.get("servlet");
        SERVER_SERVLET_CONTEXT_PATH = (String) server_servlet.getOrDefault("context-path", "/psmgr");

        Map myConfig = (Map) base.get("myConfig");
        TEST_MODE = (boolean) myConfig.getOrDefault("test-mode", false);
        localIpv4 = (String) myConfig.getOrDefault("local-ipv4", "192.168.1.214");
        FFMPEG_PATH = (String) myConfig.getOrDefault("ffmpeg-path", "ffmpeg");
        PSLOG_PATH = (String) myConfig.getOrDefault("pslog-path", "/home/stgoder/psmgr/record");
        File pslogDir = new File(PSLOG_PATH);
        if (!pslogDir.exists())
            pslogDir.mkdirs();

        RELAY_SERVER_PORT = (int) myConfig.getOrDefault("relay-server-port", 10047);
        MPEGTS_SERVER_PORT = (int) myConfig.getOrDefault("mpegts-server-port", 10048);
    }

    private Constants() {
    }
}
