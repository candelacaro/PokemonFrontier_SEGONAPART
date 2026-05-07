package logic;

import java.io.*;
import java.util.Properties;

public class ConfigManager {

    private static final String CONFIG_FILE = "config.properties";

    private Properties props = new Properties();

    public ConfigManager() {
        load();
    }

    
    private void load() {
        try (InputStream is = new FileInputStream(CONFIG_FILE)) {
            props.load(is);
        } catch (IOException e) {
            System.out.println("No se encontró config.properties, se crea uno nuevo");

            // valores por defecto
            props.setProperty("idioma", "Catala");
            props.setProperty("colorPuntuacio", "WHITE");
            props.setProperty("volumen", "70");

            save(); // lo crea
        }
    }

   
    public String getIdioma() {
        return props.getProperty("idioma", "Catala");
    }

    public String getColorPuntuacio() {
        return props.getProperty("colorPuntuacio", "WHITE");
    }

    public int getVolumen() {
        return Integer.parseInt(props.getProperty("volumen", "70"));
    }

  
    public void setColorPuntuacio(String color) {
        props.setProperty("colorPuntuacio", color);
        save();
    }

 
    private void save() {
        try (OutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "Configuración del juego");
        } catch (IOException e) {
            System.out.println("Error guardando config.properties");
            e.printStackTrace();
        }
    }
}