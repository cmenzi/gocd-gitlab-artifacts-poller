package com.buhlergroup.devops.gocd;

import com.buhlergroup.devops.gocd.message.PackageMaterialProperties;
import com.buhlergroup.devops.gocd.message.PackageMaterialProperty;
import com.buhlergroup.devops.gocd.message.ValidationResultMessage;

public class PackageRepositoryConfigurationProvider {

	public PackageMaterialProperties repositoryConfiguration() {
		PackageMaterialProperties repositoryConfigurationResponse = new PackageMaterialProperties();
		repositoryConfigurationResponse.addPackageMaterialProperty(Constants.GITLAB_SERVERURL, url());
		repositoryConfigurationResponse.addPackageMaterialProperty(Constants.DEPLOY_KEY, deployKey());
		return repositoryConfigurationResponse;
	}

	public PackageMaterialProperties packageConfiguration() {
		PackageMaterialProperties packageConfigurationResponse = new PackageMaterialProperties();
		packageConfigurationResponse.addPackageMaterialProperty(Constants.PACKAGE_SPEC, packageSpec());
		packageConfigurationResponse.addPackageMaterialProperty(Constants.PACKAGE_SPEC, packageSpec());
		packageConfigurationResponse.addPackageMaterialProperty(Constants.PACKAGE_SPEC, packageSpec());
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
		return new PackageMaterialProperty().withDisplayName("GitServer URL").withDisplayOrder("0");
	}

	private PackageMaterialProperty deployKey() {
		return new PackageMaterialProperty()
				.withRequired(true)
				.withPartOfIdentity(false)
				.withSecure(true)
				.withDisplayName("DeployKey")
				.withDisplayOrder("1");
	}

	private PackageMaterialProperty packageSpec() {
		return new PackageMaterialProperty().withDisplayName("Package Spec").withDisplayOrder("0");
	}
}
