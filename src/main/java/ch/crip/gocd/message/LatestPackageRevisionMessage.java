package ch.crip.gocd.message;

import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LatestPackageRevisionMessage {

	@Expose
	@SerializedName("repository-configuration")
	private Map<String, PackageMaterialProperty> repositoryConfiguration;

	@Expose
	@SerializedName("package-configuration")
	private Map<String, PackageMaterialProperty> packageConfiguration;

	public PackageMaterialProperties getRepositoryConfiguration() {
		return new PackageMaterialProperties(repositoryConfiguration);
	}

	public PackageMaterialProperties getPackageConfiguration() {
		return new PackageMaterialProperties(packageConfiguration);
	}
}
