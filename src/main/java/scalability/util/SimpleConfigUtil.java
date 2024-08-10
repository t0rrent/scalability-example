package scalability.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SimpleConfigUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleConfigUtil.class);
	
	private static final String CONFIG_READ_FAILURE_MESSAGE = "Config read failure";

	private static final String SERVER_CONFIG_FILE = "config.json";

	public static String getConfig(final String key, final String defaultValue) {
		try {
			return getConfg(key, defaultValue, JsonNode::asText);
		} catch (final IOException e) {
			LOGGER.error(CONFIG_READ_FAILURE_MESSAGE, e);
			return defaultValue;
		}
	}

	public static int getConfig(final String key, final int defaultValue) {
		try {
			return getConfg(key, defaultValue, JsonNode::intValue);
		} catch (final IOException e) {
			LOGGER.error(CONFIG_READ_FAILURE_MESSAGE, e);
			return defaultValue;
		}
	}

	private static <T> T getConfg(final String key, final T defaultValue, final Function<JsonNode, T> mapper) throws IOException {
		final ObjectMapper basicConfigReader = new ObjectMapper();
		final File file = new File(SERVER_CONFIG_FILE);
		if (file.exists()) {
			final JsonNode config = basicConfigReader.readTree(file);
			if (config.has(key)) {
				return mapper.apply(config.get(key));
			}
		} else {
			final InputStream resourceStream = SimpleConfigUtil.class.getClassLoader().getResourceAsStream(SERVER_CONFIG_FILE);
			if (resourceStream != null) {
				final JsonNode config = basicConfigReader.readTree(resourceStream);
				if (config.has(key)) {
					return mapper.apply(config.get(key));
				}
			}
		}
		return defaultValue;
	}

}
