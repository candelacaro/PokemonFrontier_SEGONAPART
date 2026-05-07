package logic;

import java.io.*;
import java.util.Properties;

public class ConfigManager {

    private static final String CONFIG_FILE = "config.properties";

    private Properties props = new Properties();

    public ConfigManager() {
        cargar();
    }

    private void cargar() {

        File archivo = new File(CONFIG_FILE);

        // si no existe, crear uno
        if (!archivo.exists()) {

            props.setProperty("idioma", "Catala");
            props.setProperty("colorPuntuacio", "WHITE");
            props.setProperty("volumen", "70");

            guardar();
        }

        // cargar archivo
        try (InputStream is = new FileInputStream(archivo)) {

            props.load(is);

        } catch (IOException e) {

            System.out.println("Error cargando config.properties");
            e.printStackTrace();
        }
    }

    public void guardar() {

        try (OutputStream out = new FileOutputStream(CONFIG_FILE)) {

            props.store(out, "Configuracion del juego");

        } catch (IOException e) {

            System.out.println("Error guardando config.properties");
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
        return Integer.parseInt(props.getProperty("volumen"));
    }



    public void setColorPuntuacio(String color) {

        props.setProperty("colorPuntuacio", color);

        guardar();
    }
}