package org.zb.plugin.putil;

/**
 * @author : ZhouBin
 */
public class Constant {

    public static final String PATH_WIN = "D:\\data\\";

    public static final String PATH_MAC = "/downloads/";

    public static String getPath() {
        String path = Constant.PATH_MAC;
        String osName = System.getProperty("os.name");
        if (osName != null && osName.startsWith("Windows")) {
            path = Constant.PATH_WIN;
        }
        return path;
    }
}
