package config;

public final class Config {

    //Server Rules
    public static final String HANDLER_URL = "socket.handler";
    public static final String BUS_ENDPOINT = "/socket/*";
    public static final String SOCKJS_URL = "socket\\..+";
    public static final int WEB_PORT = 8025;
    public static final String STATIC_ENDPOINT = "/*";
    public static final String DB_PORT = "9024";

    private Config() {
    }
}
