package io.github.orlouge.nomobfarm;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class NoMobFarmMod implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("nomobfarm");
	public static final String CONFIG_FNAME = "config/nomobfarm.properties";

	public static int NATURAL_SLOWDOWN_RATE = 1000;
	public static float NATURAL_RECOVERY_RATE = 0.0003f;
	public static int NATURAL_MAX_WAIT = 100000;
	public static int NATURAL_MIN_DEATHS = 0;
	public static int NATURAL_MAX_DEATHS = -1;
	public static int NATURAL_OFFLINE_PERSISTENCE = 3 * 24 * 60 * 60;

	public static int SPAWNER_SLOWDOWN_RATE = 30;
	public static float SPAWNER_RECOVERY_RATE = 0.001f;
	public static int SPAWNER_MAX_WAIT = 10000;
	public static int SPAWNER_MIN_DEATHS = 15;
	public static int SPAWNER_MAX_DEATHS = -1;
	public static int SPAWNER_OFFLINE_PERSISTENCE = 3 * 24 * 60 * 60;

	public static int RAID_MIN_SIZE = 512;
	public static int RAID_CENTER_MIN_RANDOMIZATION = 16;
	public static int RAID_CENTER_MAX_RANDOMIZATION = 24;
	public static boolean RAID_NO_BAD_OMEN_LOOP = true;

	public static long GOLEM_DETECTION_MEMORY = 10000;

	@Override
	public void onInitialize() {
		Properties defaultProps = new Properties();

		defaultProps.setProperty("natural_slowdown_rate", Integer.toString(NATURAL_SLOWDOWN_RATE));
		defaultProps.setProperty("natural_recovery_rate", Float.toString(NATURAL_RECOVERY_RATE));
		defaultProps.setProperty("natural_max_wait", Integer.toString(NATURAL_MAX_WAIT));
		defaultProps.setProperty("natural_min_deaths", Integer.toString(NATURAL_MIN_DEATHS));
		defaultProps.setProperty("natural_max_deaths", Integer.toString(NATURAL_MAX_DEATHS));
		defaultProps.setProperty("natural_offline_persistence", Integer.toString(NATURAL_OFFLINE_PERSISTENCE));
		defaultProps.setProperty("spawner_slowdown_rate", Integer.toString(SPAWNER_SLOWDOWN_RATE));
		defaultProps.setProperty("spawner_recovery_rate", Float.toString(SPAWNER_RECOVERY_RATE));
		defaultProps.setProperty("spawner_max_wait", Integer.toString(SPAWNER_MAX_WAIT));
		defaultProps.setProperty("spawner_min_deaths", Integer.toString(SPAWNER_MIN_DEATHS));
		defaultProps.setProperty("spawner_max_deaths", Integer.toString(SPAWNER_MAX_DEATHS));
		defaultProps.setProperty("spawner_offline_persistence", Integer.toString(SPAWNER_OFFLINE_PERSISTENCE));
		defaultProps.setProperty("raid_min_size", Integer.toString(RAID_MIN_SIZE));
		defaultProps.setProperty("raid_center_min_randomization", Integer.toString(RAID_CENTER_MIN_RANDOMIZATION));
		defaultProps.setProperty("raid_center_max_randomization", Integer.toString(RAID_CENTER_MAX_RANDOMIZATION));
		defaultProps.setProperty("raid_no_bad_omen_loop", Boolean.toString(RAID_NO_BAD_OMEN_LOOP));
		defaultProps.setProperty("golem_detection_memory", Long.toString(GOLEM_DETECTION_MEMORY));

		File f = new File(CONFIG_FNAME);
		if (f.isFile() && f.canRead()) {
			try (FileInputStream in = new FileInputStream(f)) {
				Properties props = new Properties(defaultProps);
				props.load(in);
				NATURAL_SLOWDOWN_RATE = Integer.parseInt(props.getProperty("natural_slowdown_rate"));
				NATURAL_RECOVERY_RATE = Float.parseFloat(props.getProperty("natural_recovery_rate"));
				NATURAL_MAX_WAIT = Integer.parseInt(props.getProperty("natural_max_wait"));
				NATURAL_MIN_DEATHS = Integer.parseInt(props.getProperty("natural_min_deaths"));
				NATURAL_MAX_DEATHS = Integer.parseInt(props.getProperty("natural_max_deaths"));
				NATURAL_OFFLINE_PERSISTENCE = Integer.parseInt(props.getProperty("natural_offline_persistence"));
				SPAWNER_SLOWDOWN_RATE = Integer.parseInt(props.getProperty("spawner_slowdown_rate"));
				SPAWNER_RECOVERY_RATE = Float.parseFloat(props.getProperty("spawner_recovery_rate"));
				SPAWNER_MAX_WAIT = Integer.parseInt(props.getProperty("spawner_max_wait"));
				SPAWNER_MIN_DEATHS = Integer.parseInt(props.getProperty("spawner_min_deaths"));
				SPAWNER_MAX_DEATHS = Integer.parseInt(props.getProperty("spawner_max_deaths"));
				SPAWNER_OFFLINE_PERSISTENCE = Integer.parseInt(props.getProperty("spawner_offline_persistence"));
				RAID_MIN_SIZE = Integer.parseInt(props.getProperty("raid_min_size"));
				RAID_CENTER_MIN_RANDOMIZATION = Integer.parseInt(props.getProperty("raid_center_min_randomization"));
				RAID_CENTER_MAX_RANDOMIZATION = Integer.parseInt(props.getProperty("raid_center_max_randomization"));
				RAID_NO_BAD_OMEN_LOOP = Boolean.parseBoolean(props.getProperty("raid_no_bad_omen_loop"));
				GOLEM_DETECTION_MEMORY = Long.parseLong(props.getProperty("golem_detection_memory"));
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
