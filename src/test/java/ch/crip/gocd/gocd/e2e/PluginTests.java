package ch.crip.gocd.gocd.e2e;


import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import ch.crip.gocd.PackageRepositoryMaterial;
import static ch.crip.gocd.PackageRepositoryMaterial.*;
import static com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("rawtypes")
public class PluginTests {

	PackageRepositoryMaterial poller;
    GoPluginApiRequest goApiPluginRequest;


    @Before
    public void setUp() {
        poller = new PackageRepositoryMaterial();
        goApiPluginRequest = mock(GoPluginApiRequest.class);
    }

    @Test
    public void shouldReturnConfigurationsWhenHandlingRepositoryConfigurationRequest() {
        String expectedRepositoryConfiguration = "{\"GITLAB_SERVERURL\":{\"display-order\":\"0\",\"display-name\":\"GitLab Server URL\",\"required\":true}," +
                "\"ACCESS_TOKEN\":{\"display-order\":\"1\",\"display-name\":\"Private Access Token\",\"part-of-identity\":false,\"secure\":true,\"required\":true}}";

        Map expectedRepositoryConfigurationMap = (Map) new GsonBuilder().create().fromJson(expectedRepositoryConfiguration, Object.class);
        when(goApiPluginRequest.requestName()).thenReturn(REQUEST_REPOSITORY_CONFIGURATION);

        GoPluginApiResponse response = poller.handle(goApiPluginRequest);
        Map responseBodyMap = (Map) new GsonBuilder().create().fromJson(response.responseBody(), Object.class);

        Assert.assertEquals(SUCCESS_RESPONSE_CODE, response.responseCode());
        Assert.assertEquals(expectedRepositoryConfigurationMap, responseBodyMap);
    }

    @Test
    public void shouldReturnNoErrorsForCorrectRepositoryConfiguration() {
        String requestBody = "{\"repository-configuration\":" +
                "{\"GITLAB_SERVERURL\":{\"value\":\"https://git.devops.buhlergroup.com\"}," +
                "\"ACCESS_TOKEN\":{\"value\":\"\"}}}";
        when(goApiPluginRequest.requestName()).thenReturn(REQUEST_VALIDATE_REPOSITORY_CONFIGURATION);
        when(goApiPluginRequest.requestBody()).thenReturn(requestBody);

        GoPluginApiResponse response = poller.handle(goApiPluginRequest);

        Assert.assertEquals(SUCCESS_RESPONSE_CODE, response.responseCode());
        Assert.assertEquals("", response.responseBody());
    }

    @Test
    public void shouldSuccessfullyConnectToRepository() {
        String requestBody = "{\"repository-configuration\":" +
                "{\"GITLAB_SERVERURL\":{\"value\":\"https://git.devops.buhlergroup.com\"}," +
                "\"ACCESS_TOKEN\":{\"value\":\"JsB-VhRYLNPcUmJaFqcH\"}}}"; // Use secret variables.
        String expectedResponseAsString = "{\"status\":\"SUCCESS\",\"messages\":[\"Connection to GitLab Server was successful\"]}";
        when(goApiPluginRequest.requestName()).thenReturn(REQUEST_CHECK_REPOSITORY_CONNECTION);
        when(goApiPluginRequest.requestBody()).thenReturn(requestBody);

        GoPluginApiResponse response = poller.handle(goApiPluginRequest);

		Map responseAsMap = (Map) new GsonBuilder().create().fromJson(response.responseBody(), Object.class);
        Map expectedResponse = (Map) new GsonBuilder().create().fromJson(expectedResponseAsString, Object.class);
        Assert.assertEquals(SUCCESS_RESPONSE_CODE, response.responseCode());
        Assert.assertEquals(expectedResponse, responseAsMap);
    }

    @Test
    public void shouldReturnConfigurationsWhenHandlingPackageConfigurationRequest() {
        String expectedPackageConfiguration = "{\"POLL_VERSION_TO\":{\"display-name\":\"Version to poll \\u003c\",\"secure\":false,\"display-order\":\"2\",\"required\":false,\"part-of-identity\":false}," +
                "\"POLL_VERSION_FROM\":{\"display-name\":\"Version to poll \\u003e\\u003d\",\"secure\":false,\"display-order\":\"1\",\"required\":false,\"part-of-identity\":false}," +
                "\"PACKAGE_ID\":{\"display-name\":\"Package ID\",\"secure\":false,\"display-order\":\"0\",\"required\":true,\"part-of-identity\":true}," +
                "\"INCLUDE_PRE_RELEASE\":{\"display-name\":\"Include Prerelease? (yes/no, defaults to yes)\",\"secure\":false,\"display-order\":\"3\",\"required\":false,\"part-of-identity\":false}}\n";
        Map expectedPackageConfigurationMap = (Map) new GsonBuilder().create().fromJson(expectedPackageConfiguration, Object.class);

        when(goApiPluginRequest.requestName()).thenReturn(REQUEST_PACKAGE_CONFIGURATION);

        GoPluginApiResponse response = poller.handle(goApiPluginRequest);
        Map responseBodyMap = (Map) new GsonBuilder().create().fromJson(response.responseBody(), Object.class);

        Assert.assertEquals(SUCCESS_RESPONSE_CODE, response.responseCode());
        Assert.assertEquals(expectedPackageConfigurationMap, responseBodyMap);
    }

    @Test
    public void shouldReturnNoErrorsForCorrectPackageConfiguration() {
        String requestBody = "{\"repository-configuration\":{\"REPO_URL\":{\"value\":\"http://nuget.org/api/v2/\"}}," +
                "\"package-configuration\":{\"PACKAGE_ID\":{\"value\":\"NUnit\"}}}";
        when(goApiPluginRequest.requestName()).thenReturn(REQUEST_VALIDATE_PACKAGE_CONFIGURATION);
        when(goApiPluginRequest.requestBody()).thenReturn(requestBody);

        GoPluginApiResponse response = poller.handle(goApiPluginRequest);

        List responseBodyList = (List) new GsonBuilder().create().fromJson(response.responseBody(), Object.class);

        Assert.assertEquals(SUCCESS_RESPONSE_CODE, response.responseCode());
        Assert.assertTrue(responseBodyList.isEmpty());
    }

    @Test
    public void shouldSuccessfullyConnectToPackage() {
        String requestBody = "{\"repository-configuration\":{\"REPO_URL\":{\"value\":\"http://nuget.org/api/v2/\"}},"+
                              "\"package-configuration\":{"+"\"PACKAGE_ID\":{\"value\":\"JQuery\"},"+
                                                            "\"POLL_VERSION_FROM\":{\"value\":\"2.2.3\"},"+
                                                            "\"POLL_VERSION_TO\":{\"value\":\"2.2.5\"}," +
                                                            "\"INCLUDE_PRE_RELEASE\":{\"value\":\"yes\"}}}\n";
        String expectedResponseAsString = "{\"messages\":[\"Successfully found revision: jQuery-2.2.4\"],\"status\":\"success\"}";
        when(goApiPluginRequest.requestName()).thenReturn(REQUEST_CHECK_PACKAGE_CONNECTION);
        when(goApiPluginRequest.requestBody()).thenReturn(requestBody);

        GoPluginApiResponse response = poller.handle(goApiPluginRequest);

        Map responseAsMap = (Map) new GsonBuilder().create().fromJson(response.responseBody(), Object.class);
        Map expectedResponse = (Map) new GsonBuilder().create().fromJson(expectedResponseAsString, Object.class);
        Assert.assertEquals(SUCCESS_RESPONSE_CODE, response.responseCode());
        Assert.assertEquals(expectedResponse, responseAsMap);

    }

    @Test
    public void getLatestRevisionShouldBeSuccessful() {
        String requestBody = "{\"repository-configuration\":" +
                "{\"GITLAB_SERVERURL\":{\"value\":\"https://git.devops.buhlergroup.com\"}," +
                "\"ACCESS_TOKEN\":{\"value\":\"JsB-VhRYLNPcUmJaFqcH\"}}," +
                "\"package-configuration\":{\"PROJECT_ID\":{\"value\":\"4\"},"+
                "\"BRANCH_NAME\":{\"value\":\"\"}}}";                
        
        when(goApiPluginRequest.requestName()).thenReturn(REQUEST_LATEST_PACKAGE_REVISION);
        when(goApiPluginRequest.requestBody()).thenReturn(requestBody);

        GoPluginApiResponse response = poller.handle(goApiPluginRequest);

        Assert.assertEquals(SUCCESS_RESPONSE_CODE, response.responseCode());
    }

    @Test
    public void getLatestRevisionSinceShouldBeSuccessful() {
        String requestBody = "{\"repository-configuration\":{\"REPO_URL\":{\"value\":\"http://nuget.org/api/v2/\"}},"+
                "\"package-configuration\":{\"PACKAGE_ID\":{\"value\":\"jQuery\"},"+
                                           "\"POLL_VERSION_FROM\":{\"value\":\"2.2.3\"},"+
                                           "\"POLL_VERSION_TO\":{\"value\":\"3\"},"+
                                           "\"INCLUDE_PRE_RELEASE\":{\"value\":\"no\"}},"+
                "\"previous-revision\":{\"revision\":\"jQuery-2.2.4\","+
                                       "\"timestamp\":\"2016-06-16T16:31:00.873Z\","+
                                       "\"data\":{\"LOCATION\":\"http://www.nuget.org/api/v2/package/jQuery/2.2.4\",\"VERSION\":\"2.2.4\"}}}";
        when(goApiPluginRequest.requestName()).thenReturn(REQUEST_LATEST_PACKAGE_REVISION_SINCE);
        when(goApiPluginRequest.requestBody()).thenReturn(requestBody);

        GoPluginApiResponse response = poller.handle(goApiPluginRequest);

        Assert.assertEquals(SUCCESS_RESPONSE_CODE, response.responseCode());
    }

}