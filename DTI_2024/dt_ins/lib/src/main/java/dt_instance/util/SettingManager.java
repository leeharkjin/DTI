package dt_instance.util;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class SettingManager {
    private static final String CONFIG_FILE_PATH = "config/config.properties";
    private Properties properties;

    public SettingManager() {
        properties = new Properties();
        loadConfig();
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
        saveConfig();
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    private void loadConfig() {
        try (FileInputStream inputStream = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveConfig() {
        try (FileOutputStream outputStream = new FileOutputStream(CONFIG_FILE_PATH)) {
            properties.store(outputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public int getInteger(String key) {
		// TODO Auto-generated method stub
		
		return Integer.parseInt(properties.getProperty(key));
	}

	public boolean getBoolean(String key) {
		// TODO Auto-generated method stub
		return Boolean.parseBoolean(properties.getProperty(key));
	}

	public String getString(String key) {
		// TODO Auto-generated method stub
		return properties.getProperty(key);
	}

	public boolean containsKey(String defRegistryHost) {
		// TODO Auto-generated method stub
		
		return properties.containsKey(defRegistryHost);
	}
}