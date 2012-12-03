package ru.histone.utils;

import java.io.IOException;
import java.util.Properties;

/**
 * @author sazonovkirill@gmail.com
 */
public class HistoneVersion {
    public static String VERSION;

    private static final String VERSION_FILE_NAME = "version.properties";
    private static final String VERSION_KEY = "histone.version";

    static {
        Properties props = new Properties();
        try {
            props.load(HistoneVersion.class.getClassLoader().getResourceAsStream(VERSION_FILE_NAME));
        } catch (IOException e) {
            System.out.println("File version.properties not found");
        }

        VERSION = props.getProperty(VERSION_KEY);
    }

    public static void main(String[] args) {
        System.out.println(HistoneVersion.VERSION);
    }
}
