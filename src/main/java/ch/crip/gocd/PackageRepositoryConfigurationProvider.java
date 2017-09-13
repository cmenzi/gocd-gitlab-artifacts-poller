package ch.crip.gocd;

import ch.crip.gocd.message.PackageMaterialProperties;
import ch.crip.gocd.message.PackageMaterialProperty;
import ch.crip.gocd.message.ValidationResultMessage;

public class PackageRepositoryConfigurationProvider {

	public PackageMaterialProperties repositoryConfiguration() {
		PackageMaterialProperties repositoryConfigurationResponse = new PackageMaterialProperties();
		repositoryConfigurationResponse.addPackageMaterialProperty(Constants.GITLAB_SERVERURL, url());
		repositoryConfigurationResponse.addPackageMaterialProperty(Constants.ACCESS_TOKEN, deployKey());
		return repositoryConfigurationResponse;
	}

	public PackageMaterialProperties packageConfiguration() {
		PackageMaterialProperties packageConfigurationResponse = new PackageMaterialProperties();
		packageConfigurationResponse.addPackageMaterialProperty(Constants.PROJECT_ID, projectId());
		packageConfigurationResponse.addPackageMaterialProperty(Constants.BRANCH_NAME, branchName());
		return packageConfigurationResponse;
	}

	public ValidationResultMessage validateRepositoryConfiguration(PackageMaterialProperties configurationProvidedByUser) {
		ValidationResultMessage validationResultMessage = new ValidationResultMessage();
		return validationResultMessage;
	}

	public ValidationResultMessage validatePackageConfiguration(PackageMaterialProperties configurationProvidedByUser) {
		ValidationResultMessage validationResultMessage = new ValidationResultMessage();
		return validationResultMessage;
	}
	
	private PackageMaterialProperty url() {
		return new PackageMaterialProperty()
				.withRequired(true)
				.withDisplayName("GitLab Server URL")
				.withDisplayOrder("0");
	}

	private PackageMaterialProperty deployKey() {
		return new PackageMaterialProperty()
				.withRequired(true)
				.withPartOfIdentity(false)
				.withSecure(true)
				.withDisplayName("Private Access Token")
				.withDisplayOrder("1");
	}
	
	
	private PackageMaterialProperty projectId() {
		return new PackageMaterialProperty()
				.withRequired(true)
				.withDisplayName("GitLab Project ID")
				.withDisplayOrder("0");
	}
	
	private PackageMaterialProperty branchName() {
		return new PackageMaterialProperty()
				.withSecure(false)
				.withRequired(false)
				.withValue("")
				.withDisplayName("Branch to include")
				.withDisplayOrder("1");
	}
	
}
