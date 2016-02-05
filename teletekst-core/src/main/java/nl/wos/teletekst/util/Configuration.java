package nl.wos.teletekst.util;

public class Configuration {
    public static final String  DATA_DIR = "/apps/wos/";
    public static final boolean DEBUG_MODE = false;
    public static final String  IP_TELETEXT_SERVER = "10.35.0.80";
    public static final int     PORT_TELETEXT_SERVER = 21;
    public static final String  FTP_USER_TELETEXT_SERVER = "FTP_2016";
    public static final String  FTP_PASSWORD_TELETEXT_SERVER = "qk34&#sdfhk123()%";
    public static final String  FTP_UPLOAD_PATH_TELETEXT_SERVER = "/PheTxtServer/Transfer/FTP_2016/";

    public static final String  RSS_FEED_IB_BROADCAST = "http://teletekst.ibbroadcast.nl/getFeed.ashx?id=190";

    public static final char    COLOR_WHITE = '\u0007';
    public static final int     PAGENUMBER_LAATSTE_NIEUWS = 101;
    public static final int     PAGENUMBER_NIEUWS_OVERZICHT = 102;
    public static final int     PAGENUMBER_NIEUWS_BERICHTEN_START = 103;
    public static final int     PAGENUMBER_NIEUWS_BERICHTEN_END = 120;
    public static final int     PAGENUMBER_SPORT_BERICHTEN_START = 650;
    public static final int     PAGENUMBER_SPORT_BERICHTEN_END = 655;

    public static final int     TEXT_PAGE_SIZE = 39;
    public static final int     MAX_TITLE_SIZE = 35;
}
