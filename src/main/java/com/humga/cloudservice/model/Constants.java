package com.humga.cloudservice.model;

public class Constants {
    public static final String TOKEN_ISSUER = "http://humgaissuer.com";
    public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 5*60*60;
    public static final String SIGNING_KEY = "humga_s1_k1";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "auth-token";
}
