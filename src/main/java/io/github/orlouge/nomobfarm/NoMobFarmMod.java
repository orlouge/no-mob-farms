package io.github.orlouge.nomobfarm;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.input.Input;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class NoMobFarmMod implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("nomobfarm");
	public static final String CONFIG_FNAME = "config/nomobfarm.properties";

	public static int SLOWDOWN_RATE = 1000;
	public static float RECOVERY_RATE = 0.0003f;
	public static int MAX_WAIT = 100000;

	@Override
	public void onInitialize() {
		Properties defaultProps = new Properties();

		defaultProps.setProperty("slowdown_rate", Integer.toString(SLOWDOWN_RATE));
		defaultProps.setProperty("recovery_rate", Float.toString(RECOVERY_RATE));
		defaultProps.setProperty("max_wait", Integer.toString(MAX_WAIT));

		File f = new File(CONFIG_FNAME);
		if (f.isFile() && f.canRead()) {
			try (FileInputStream in = new FileInputStream(f)) {
				Properties props = new Properties(defaultProps);
				props.load(in);
				SLOWDOWN_RATE = Integer.parseInt(props.getProperty("slowdown_rate"));
				RECOVERY_RATE = Float.parseFloat(props.getProperty("recovery_rate"));
				MAX_WAIT = Integer.parseInt(props.getProperty("max_wait"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try (FileOutputStream out = new FileOutputStream(CONFIG_FNAME)) {
				defaultProps.store(out, "");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
