package fun.stgoder.jsmpeg_relay.common.util;

public class Util {
    public static String getParamFromUri(String uri, String paramName) {
        uri = uri.replace("/?", "");
        String[] strs = uri.split("&");
        for (String str : strs) {
            if (str == null || str == "")
                continue;
            String[] kvs = str.split("=");
            if (kvs.length != 2)
                continue;
            String k = kvs[0];
            if (k != null && k.equals(paramName)) {
                return kvs[1];
            }
        }
        return null;
    }
}
