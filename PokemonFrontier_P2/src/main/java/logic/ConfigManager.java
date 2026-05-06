package logic;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {

    private Properties props = new Properties();
    
    public ConfigManager() {
        try {
            FileInputStream fis = new FileInputStream("config.properties");
            props.load(fis);
        } catch (IOException e) {
            System.out.println("Error cargant config.properties");
            e.printStackTrace();
        }
    }

    public String getIdioma() {
        return props.getProperty("idioma");
    }

    public String getColorPuntuacio() {
        return props.getProperty("colorPuntuacio");
    }

    public int getVolumen() {
        return Integer.parseInt(props.getProperty("volumen", "70"));
    }
}