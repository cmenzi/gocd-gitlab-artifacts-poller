package ch.crip.gocd;

import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.logging.Logger;

public class JsonUtil {
	private static final Logger LOGGER = Logger.getLoggerFor(PackageRepositoryPoller.class);
	
    public static String toJsonString(Object object) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String json = gsonBuilder.create().toJson(object);
        
        LOGGER.info("ToJSON: " + json);
        
        return json;
    }

    public static <T> T fromJsonString(String json, Class<T> type) {
    	LOGGER.info("fromJSON: " + json);
    	
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return gsonBuilder.create().fromJson(json, type);
    }
}
