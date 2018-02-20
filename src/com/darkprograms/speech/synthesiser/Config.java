package com.darkprograms.speech.synthesiser;

/**
 *
 * @author eduardo
 */
public class Config {
    public static String database = "jdbc:postgresql://127.0.0.1:5432/subtitledb";
    public static String dbUser = "postgres";
    public static String dbPassword = "postgres";
    
    public static int MAX_SIMULTANEOUS_REQUESTS = 5;
    
    public static int SAMPLE_RATE = 44100;
    //public static int SAMPLE_RATE = 22050;
    
    //public static String LANGUAGE = "en-US";
    public static String LANGUAGE = "pt-BR";
}
