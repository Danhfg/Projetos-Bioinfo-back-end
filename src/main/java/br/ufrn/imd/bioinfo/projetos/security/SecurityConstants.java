package br.ufrn.imd.bioinfo.projetos.security;

public class SecurityConstants {

    static final String SECRET = "teste";
    static final String TOKEN_PREFIX = "Bearer";
    static final String HEADER_STRING = "Authorization";
    public static final String SIGN_IN_URL = "/daniel_backend/api/sign-in/**";
    public static final String SIGN_UP_URL = "/daniel_backend/api/sign-up/**";
    static final Long EXPIRATION_TIME = 10800000L;

}
