package logic;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

    private Properties props = new Properties();

    public ConfigManager() {
        try {
            InputStream is = getClass()
                    .getClassLoader()
                    .getResourceAsStream("config.properties");

            if (is != null) {
                props.load(is);
            } else {
                System.out.println("No se encontró config.properties");
            }

        } catch (IOException e) {
            System.out.println("Error cargando config.properties");
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

