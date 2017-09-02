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
		packageConfigurationResponse.addPackageMaterialProperty(Constants.JOB_NAME, jobName());
		packageConfigurationResponse.addPackageMaterialProperty(Constants.ARTIFACT_PATH, artifactPath());
		packageConfigurationResponse.addPackageMaterialProperty(Constants.ARTIFACT_PATTERN, artifactPattern());
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
		return new PackageMaterialProperty().withDisplayName("GitLab Server URL").withDisplayOrder("0");
	}

	private PackageMaterialProperty deployKey() {
		return new PackageMaterialProperty()
				.withRequired(true)
				.withPartOfIdentity(false)
				.withSecure(true)
				.withDisplayName("Access Token")
				.withDisplayOrder("1");
	}
	
	private PackageMaterialProperty projectId() {
		return new PackageMaterialProperty()
				.withRequired(true)
				.withDisplayName("Project Id")
				.withDisplayOrder("0");
	}

	private PackageMaterialProperty jobName() {
		return new PackageMaterialProperty()
				.withRequired(false)
				.withValue("")
				.withDisplayName("Job Name")
				.withDisplayOrder("0");
	}
	
	private PackageMaterialProperty artifactPath() {
		return new PackageMaterialProperty()
				.withSecure(false)
				.withRequired(false)
				.withDisplayName("Artifact Path")
				.withDisplayOrder("1");
	}
	
	private PackageMaterialProperty artifactPattern() {
		return new PackageMaterialProperty()
				.withSecure(false)
				.withRequired(false)
				.withDisplayName("Artifact Pattern")
				.withDisplayOrder("2");
	}
	
}
