package ch.crip.gocd;

import static java.util.Arrays.asList;

import java.util.Date;
import java.util.List;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.Constants.JobScope;
import org.gitlab4j.api.models.ArtifactsFile;
import org.gitlab4j.api.models.Job;
import org.gitlab4j.api.models.Version;

import com.thoughtworks.go.plugin.api.logging.Logger;

import ch.crip.gocd.message.CheckConnectionResultMessage;
import ch.crip.gocd.message.PackageMaterialProperties;
import ch.crip.gocd.message.PackageRevisionMessage;

import static ch.crip.gocd.JsonUtil.toJsonString;

public class PackageRepositoryPoller {

	private static final Logger LOGGER = Logger.getLoggerFor(PackageRepositoryPoller.class);
	
	public PackageRepositoryPoller(PackageRepositoryConfigurationProvider configurationProvider) {
	}

	public CheckConnectionResultMessage checkConnectionToRepository(PackageMaterialProperties repositoryConfiguration) {
		try {
			LOGGER.info("checkConnectionToRepository: " + toJsonString(repositoryConfiguration));
			
			GitLabApi gitLabApi = createGitLabApi(repositoryConfiguration);
			Version version = gitLabApi.getVersion();
			
			LOGGER.info("Connection to GitLab Server: " + version.getVersion());

			return new CheckConnectionResultMessage(CheckConnectionResultMessage.STATUS.SUCCESS, asList("Connection to GitLab Server was successful"));
		} catch (GitLabApiException e) {
			return new CheckConnectionResultMessage(CheckConnectionResultMessage.STATUS.FAILURE, asList("Connection error", e.toString()));
		}
	}

	public CheckConnectionResultMessage checkConnectionToPackage(PackageMaterialProperties packageConfiguration, PackageMaterialProperties repositoryConfiguration) {
		try {
			LOGGER.info("checkConnectionToPackage REPO: " + toJsonString(repositoryConfiguration) + "\n PACK: " + toJsonString(repositoryConfiguration));
			
			int projectId = Integer.parseInt(packageConfiguration.getProperty(Constants.PROJECT_ID).value());
			GitLabApi gitLabApi = createGitLabApi(repositoryConfiguration);
			Job lastestJob = getLatestSuccessfulJob(packageConfiguration, gitLabApi, projectId);
			if (lastestJob == null) {
				return new CheckConnectionResultMessage(CheckConnectionResultMessage.STATUS.FAILURE, asList("Package not found, because no job exists."));
			}

			ArtifactsFile artifactsFile = lastestJob.getArtifactsFile();
			if (artifactsFile == null || artifactsFile.getFilename() == null) {
				return new CheckConnectionResultMessage(CheckConnectionResultMessage.STATUS.FAILURE, asList("Package not found, because artifact does not exits."));
			}

			return new CheckConnectionResultMessage(CheckConnectionResultMessage.STATUS.SUCCESS, asList("Package connection successful"));
		} catch (GitLabApiException e) {
			return new CheckConnectionResultMessage(CheckConnectionResultMessage.STATUS.FAILURE, asList("Connection error", e.toString()));
		}
	}

	public PackageRevisionMessage getLatestRevision(PackageMaterialProperties packageConfiguration, PackageMaterialProperties repositoryConfiguration) {
		try {
			
			LOGGER.info("getLatestRevision REPO: " + toJsonString(repositoryConfiguration) + "\n PACK: " + toJsonString(repositoryConfiguration));
			
			int projectId = Integer.parseInt(packageConfiguration.getProperty(Constants.PROJECT_ID).value());
			String branchName = "";
			if(packageConfiguration.hasKey(Constants.BRANCH_NAME)) {
				branchName = packageConfiguration.getProperty(Constants.BRANCH_NAME).value();	
			}
			
			LOGGER.info(String.format("Check for latest revision: ProjectId = '%s', Branch = '%s'", projectId, branchName));

			GitLabApi gitLabApi = createGitLabApi(repositoryConfiguration);
			Job latestJob = getLatestSuccessfulJob(packageConfiguration, gitLabApi, projectId);

			String revision = latestJob.getId().toString();
			Date timestamp = latestJob.getCreatedAt();
			String user = latestJob.getUser().getUsername();
			String revisionComment = latestJob.getCommit().getMessage();
			String trackbackUrl = gitLabApi.getProjectApi().getProject(projectId).getWebUrl();
			String artifactLocationUrl = createArtifactLocationUrl(gitLabApi, latestJob, projectId);
			String artifactBranch = latestJob.getRef();
			String artifactCommit = latestJob.getCommit().getId();
			
			LOGGER.info(String.format("Create package revision message: Revision = '%s', Timestamp = '%s', User = '%s', Comment = '%s', TrackbackUrl = '%s'", 
					revision,
					timestamp,
					user,
					revisionComment,
					trackbackUrl
					));

			PackageRevisionMessage packageRevisionMessage = new PackageRevisionMessage(revision, timestamp, user, revisionComment, trackbackUrl);
			packageRevisionMessage.addData(Constants.PACKAGE_LOCATION, artifactLocationUrl);
			packageRevisionMessage.addData(Constants.PACKAGE_BRANCH, artifactBranch);
			packageRevisionMessage.addData(Constants.PACKAGE_COMMIT, artifactCommit);
			
			if (branchName == null || branchName.isEmpty()) {
				LOGGER.info("no branch specified");
				return packageRevisionMessage;
			}

			LOGGER.info(String.format("check branchName='%s' is equal to ref='%s'", branchName.toLowerCase(), latestJob.getRef().toLowerCase()));
			if (branchName.toLowerCase().equals(latestJob.getRef().toLowerCase())) {
				return packageRevisionMessage;
			}
			
		} catch (Exception e) {
			LOGGER.error("ERROR to get latest revision", e);
		}
		
		return null;
	}

	public PackageRevisionMessage getLatestRevisionSince(PackageMaterialProperties packageConfiguration, PackageMaterialProperties repositoryConfiguration, PackageRevisionMessage previousPackageRevision) {
		LOGGER.info("getLatestRevisionSince REPO: " + toJsonString(repositoryConfiguration) + "\n PACK: " + toJsonString(repositoryConfiguration));
		
		PackageRevisionMessage prm = getLatestRevision(packageConfiguration, repositoryConfiguration);
		if (prm.getTimestamp().after(previousPackageRevision.getTimestamp())) {
			return prm;
		}
		
		return null;
	}

	private GitLabApi createGitLabApi(PackageMaterialProperties repositoryConfiguration) {
		GitLabApi gitLabApi = new GitLabApi(repositoryConfiguration.getProperty(Constants.GITLAB_SERVERURL).value(), repositoryConfiguration.getProperty(Constants.ACCESS_TOKEN).value());
		return gitLabApi;
	}

	private Job getLatestSuccessfulJob(PackageMaterialProperties packageConfiguration, GitLabApi gitLabApi, Integer projectId) throws GitLabApiException {
		LOGGER.info("getLatestSuccessfulJob: " + projectId);
		
		List<Job> jobs = gitLabApi.getJobApi().getJobs(projectId, JobScope.SUCCESS);
		Job lastestJob = jobs.get(0);
		return lastestJob;
	}

	private String createArtifactLocationUrl(GitLabApi gitLabApi, Job job, Integer projectId) throws GitLabApiException {
		String url = gitLabApi.getProjectApi().getProject(projectId).getWebUrl() + "/-/jobs/" + job.getId() + "/artifacts/download";
		return url;
	}
}
