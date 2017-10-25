#!/usr/bin/groovy

/**
 * Performs a release on https://oss.sonatype.org.
 * @param parameters    release parameters.
 * @return
 */
def call(Map parameters = [:]) {
    def enabled = parameters.get('enabled', 'true')
    if ('false'.equalsIgnoreCase(enabled)) {
       return null;
    }

    def nexusUrl = parameters.get('nexusUrl', 'https://oss.sonatype.org')
    def nexusStagingPluginVersion = parameters.get('nexusStagingPluginVersion', '1.6.7')
    def nexusServerId = parameters.get('nexusServerId', 'oss-sonatype-staging')

    def stagingProgressTimeoutMinutes = parameters.get('stagingProgressTimeoutMinutes', '5')
    def stagingProgressPauseDurationSeconds = parameters.get('stagingProgressPauseDurationSeconds', '3')
    def autoReleaseAfterClose = parameters.get('autoReleaseAfterClose', 'false')
    def autoDropAfterRelease = parameters.get('autoDropAfterRelease', 'true')

    def dockerServerId = parameters.get('dockerServerId', 'dockerhub')
    def dockerRegistry = parameters.get('dockerRegisty', 'docker.io')
    def profiles = parameters.get('profiles', 'release,fabric8')
    def branch = parameters.get('branch', 'master')

    def pom = readMavenPom file: 'pom.xml'
    def groupId = pom.groupId
    def version = pom.version

    def releaseVersion = parameters.get('releaseVersion', '')
    def developmentVersion = parameters.get('developmentVersion', '')

    def mavenOptions = ''

    if (!releaseVersion.isEmpty()) {
      mavenOptions += " -DreleaseVersion=${releaseVersion}"
    }

    if (!developmentVersion.isEmpty()) {
      mavenOptions += " -DdevelopmentVersion=${developmentVersion}"
    }


    sh "git checkout ${branch}"

    //Perform traditional release and deploy to sonatype
    sh "mvn -B release:clean release:prepare release:perform -DpushChanges=false -Dtag=${releaseVersion} -P${profiles} ${mavenOptions}"

    //List all repositories and grab the output.
    def stagingRepositoryPrefix = groupId.replaceAll("\\.","")
    def stagingRepositoryInfo = sh(returnStdout: true,
                                   script: "mvn org.sonatype.plugins:nexus-staging-maven-plugin:${nexusStagingPluginVersion}:rc-list -DnexusUrl=${nexusUrl} -DserverId=${nexusServerId} | grep ${stagingRepositoryPrefix} | head -n 1").trim().split("[ ]+")

    if (stagingRepositoryInfo.length < 3) {
      throw new IllegalStateException("Failed to determine staging repository id.")
    }

    def stagingRepositoryId = stagingRepositoryInfo[1]
    def stagingRepositoryState = stagingRepositoryInfo[2]

    //Close the repository
    if (!'closed'.equalsIgnoreCase(stagingRepositoryState)) {
      sh "mvn org.sonatype.plugins:nexus-staging-maven-plugin:${nexusStagingPluginVersion}:rc-close -DnexusUrl=${nexusUrl} -DserverId=${nexusServerId} -DstagingRepositoryId=${stagingRepositoryId} -DstagingProgressTimeoutMinutes=${stagingProgressTimeoutMinutes} -DstagingProgressPauseDurationSeconds=${stagingProgressPauseDurationSeconds} -DautoReleaseAfterClose=${autoReleaseAfterClose}"
    }

    //Release the repository
    if (!'true'.equalsIgnoreCase(autoReleaseAfterClose)) {
      sh "mvn org.sonatype.plugins:nexus-staging-maven-plugin:${nexusStagingPluginVersion}:rc-release -DnexusUrl=${nexusUrl} -DserverId=${nexusServerId} -DstagingRepositoryId=${stagingRepositoryId} -DstagingProgressTimeoutMinutes=${stagingProgressTimeoutMinutes} -DstagingProgressPauseDurationSeconds=${stagingProgressPauseDurationSeconds} -DautoDropAfterRelease=${autoDropAfterRelease}"
    }

    sh "git push origin ${branch}"
    sh "git push origin --tags"

    return getLatestMavenReleaseVersion()
}
