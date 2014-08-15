package exec.download;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class UConfig {
	private static final String DL_CONFIG_PATH = "/download_config.properties";
	static Properties p;

	static {
		loadDownLoadProperties();
	}

	public static String getProperty(String name){
		return p.getProperty(name);
	}

	public static void loadDownLoadProperties() {
		InputStream in = UConfig.class.getResourceAsStream(DL_CONFIG_PATH);
		try {
			p = new Properties();
			p.load(in);
		} catch (IOException e) {
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
			}
		}
	}
}
