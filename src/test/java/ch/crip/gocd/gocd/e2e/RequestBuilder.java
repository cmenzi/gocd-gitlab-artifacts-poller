package ch.crip.gocd.gocd.e2e;

import java.util.HashMap;
import java.util.Map;

import static ch.crip.gocd.PackageRepositoryMaterial.*;

public class RequestBuilder {

    private Map<String, Object> request;

    public RequestBuilder() {
        request = new HashMap<String, Object>();
    }

    public RequestBuilder withRespositoryConfiguration(String url, String username, String password) {
        Map<String, Object> urlMap = new HashMap<String, Object>();
        urlMap.put("value", url);
        Map<String, Object> fieldsMap = new HashMap<String, Object>();
        fieldsMap.put("REPO_URL", urlMap);
        Map<String, Object> usernameMap = new HashMap<String, Object>();
        usernameMap.put("value", username);
        fieldsMap.put("USERNAME", usernameMap);
        Map<String, Object> passwordMap = new HashMap<String, Object>();
        passwordMap.put("value", password);
        fieldsMap.put("PASSWORD", passwordMap);
        request.put(REQUEST_REPOSITORY_CONFIGURATION, fieldsMap);
        return this;
    }

    public RequestBuilder withPackageConfiguration(String packageID) {
    	Map<String, Object> packageIDMap = new HashMap<String, Object>();
        packageIDMap.put("value", packageID);

        Map<String, Object> packageConfigurationMap = new HashMap<String, Object>();
        packageConfigurationMap.put("PACKAGE_ID", packageIDMap);

        request.put(REQUEST_PACKAGE_CONFIGURATION, packageConfigurationMap);
        return this;
    }

    public RequestBuilder withPreviousRevision(String version) {
    	Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("VERSION", version);
        Map<String, Object> revisionInfoMap = new HashMap<String, Object>();
        revisionInfoMap.put("data", dataMap);
        revisionInfoMap.put("timestamp", "2011-07-14T19:43:37.100Z");
        revisionInfoMap.put("revision", "abc-10.2.1.rpm");
        request.put("previous-revision", revisionInfoMap);
        return this;
    }

    public Map<String, Object> build() {
        return request;
    }

}
