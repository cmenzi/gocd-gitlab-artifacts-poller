package ch.crip.gocd.message;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RepositoryConnectionMessage {

    @Expose
    @SerializedName("repository-configuration")
    private Map<String,PackageMaterialProperty> repositoryConfiguration;

    public PackageMaterialProperties getRepositoryConfiguration() {
        return new PackageMaterialProperties(repositoryConfiguration);
    }
}
