package com.buhlergroup.devops.gocd;

import static java.util.Arrays.asList;

import com.buhlergroup.devops.gocd.message.CheckConnectionResultMessage;
import com.buhlergroup.devops.gocd.message.PackageMaterialProperties;
import com.buhlergroup.devops.gocd.message.PackageRevisionMessage;

public class PackageRepositoryPoller {

	public PackageRepositoryPoller(PackageRepositoryConfigurationProvider configurationProvider) {
	}

	public CheckConnectionResultMessage checkConnectionToRepository(PackageMaterialProperties repositoryConfiguration) {
		return new CheckConnectionResultMessage(CheckConnectionResultMessage.STATUS.SUCCESS, asList("success message"));
	}

	public CheckConnectionResultMessage checkConnectionToPackage(PackageMaterialProperties packageConfiguration, PackageMaterialProperties repositoryConfiguration) {
		return new CheckConnectionResultMessage(CheckConnectionResultMessage.STATUS.SUCCESS, asList("success message"));
	}

	public PackageRevisionMessage getLatestRevision(PackageMaterialProperties packageConfiguration, PackageMaterialProperties repositoryConfiguration) {
		return new PackageRevisionMessage();
	}

	public PackageRevisionMessage getLatestRevisionSince(PackageMaterialProperties packageConfiguration, PackageMaterialProperties repositoryConfiguration, PackageRevisionMessage previousPackageRevision) {
		return new PackageRevisionMessage();
	}
}
