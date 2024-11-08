package DB;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;

public class DB_Connection {
    private static final Properties properties = new Properties();
    static {
        try {
            // Load properties from file
            loadProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadProperties() throws IOException {
        String env = System.getProperty("env", "local"); // Default to local if not specified
        String configFile = String.format("/home/john/CC214_projects/Library_management_system/config.properties", env);

        try (FileInputStream input = new FileInputStream(configFile)) {
            properties.load(input);
        }
    }
    public static String url = properties.getProperty("db.url");
    public static String user = properties.getProperty("db.user");
    public static String pass = properties.getProperty("db.password");
    public static String tab = properties.getProperty("db.table");
}
