package uoscs.capstone.allyojo.config.jwt;

public class JwtProperties {
    public static final String SECRET = "allyojo";
    public static final long EXPIRATION_TIME = 86400 * 10; // 10일
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
}
