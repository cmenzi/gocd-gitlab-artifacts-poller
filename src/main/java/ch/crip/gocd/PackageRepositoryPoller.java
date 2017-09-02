package ch.crip.gocd;

import static java.util.Arrays.asList;
import java.io.File;
import java.io.FilenameFilter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.Constants.JobScope;
import org.gitlab4j.api.models.ArtifactsFile;
import org.gitlab4j.api.models.Job;
import org.gitlab4j.api.models.Project;

import ch.crip.gocd.message.CheckConnectionResultMessage;
import ch.crip.gocd.message.PackageMaterialProperties;
import ch.crip.gocd.message.PackageRevisionMessage;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class PackageRepositoryPoller {

	public PackageRepositoryPoller(PackageRepositoryConfigurationProvider configurationProvider) {
	}

	public CheckConnectionResultMessage checkConnectionToRepository(PackageMaterialProperties repositoryConfiguration) {
		GitLabApi gitLabApi = new GitLabApi(repositoryConfiguration.getProperty(Constants.GITLAB_SERVERURL).value(), repositoryConfiguration.getProperty(Constants.ACCESS_TOKEN).value());
		try {
			gitLabApi.getVersion();
			return new CheckConnectionResultMessage(CheckConnectionResultMessage.STATUS.SUCCESS, asList("Connection successful"));
		} catch (GitLabApiException e) {
			return new CheckConnectionResultMessage(CheckConnectionResultMessage.STATUS.FAILURE, asList("Connection error", e.toString()));
		}
	}

	public CheckConnectionResultMessage checkConnectionToPackage(PackageMaterialProperties packageConfiguration, PackageMaterialProperties repositoryConfiguration) {
		GitLabApi gitLabApi = new GitLabApi(repositoryConfiguration.getProperty(Constants.GITLAB_SERVERURL).value(), repositoryConfiguration.getProperty(Constants.ACCESS_TOKEN).value());
		try {
			int projectId = Integer.parseInt(packageConfiguration.getProperty(Constants.PROJECT_ID).value());
			List<Job> jobs = gitLabApi.getJobApi().getJobs(projectId, JobScope.SUCCESS);
			Job lastestJob = jobs.get(0);
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
		String gitlabServerUrl = repositoryConfiguration.getProperty(Constants.GITLAB_SERVERURL).value();
		GitLabApi gitLabApi = new GitLabApi(gitlabServerUrl, repositoryConfiguration.getProperty(Constants.ACCESS_TOKEN).value());
		try {
			int projectId = Integer.parseInt(packageConfiguration.getProperty(Constants.PROJECT_ID).value());
			Project project = gitLabApi.getProjectApi().getProject(projectId);
			String artifactPath = packageConfiguration.getProperty(Constants.ARTIFACT_PATH).value();
			String artifactPattern = packageConfiguration.getProperty(Constants.ARTIFACT_PATTERN).value();

			List<Job> jobs = gitLabApi.getJobApi().getJobs(projectId, JobScope.SUCCESS);
			Job lastestJob = jobs.get(0);

			File artifactsFile = gitLabApi.getJobApi().downloadArtifactsFile(projectId, lastestJob.getRef(), lastestJob.getName(), null);
			ZipFile zipFile = new ZipFile(artifactsFile.getAbsolutePath());
			String parentDir = artifactsFile.getParentFile().getAbsolutePath();
			String extractDir = parentDir + "./" + artifactsFile.getName() + "-extracted";
			
			zipFile.extractAll(extractDir);
			File file = new File(extractDir);
			File[] files = file.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.matches(artifactPattern);
				}
			});

			if (files.length >= 1) {
				File artifactFile = files[0];
				String artifactFileName = artifactFile.getName();
				String versionString = artifactFileName.substring(artifactFileName.indexOf("-"));				
				String fileName = artifactPath + "./" + artifactFile.getName();
				String revision = versionString;
				String trackbackUrl = createArtifactRawUrl(project.getWebUrl(), lastestJob.getId(), fileName);
				return new PackageRevisionMessage(revision, lastestJob.getFinishedAt(), lastestJob.getUser().getName(), "", trackbackUrl);
			}

			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public PackageRevisionMessage getLatestRevisionSince(PackageMaterialProperties packageConfiguration, PackageMaterialProperties repositoryConfiguration,
			PackageRevisionMessage previousPackageRevision) {
		return null;
	}
	
	private static String createArtifactRawUrl(String projectUrl, Integer jobId, String artifactsPath)
	{
		String url = projectUrl + "-/jobs/" + jobId + "/artifacts/file/" + artifactsPath;
		return url;
	}
}
