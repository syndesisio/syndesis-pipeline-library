#!/usr/bin/groovy

/**
 * Performs a release on https://oss.sonatype.org.
 * @param parameters    release parameters.
 * @return
 */
def call(Map parameters = [:]) {
    def nexusUrl = parameters.get('nexusUrl', 'https://oss.sonatype.org')
    def nexusStagingPluginVersion = parameters.get('nexusStagingPluginVersion', '1.6.7')
    def nexusServerId = parameters.get('nexusServerId', 'oss-sonatype-staging')
    def dockerServerId = parameters.get('dockerServerId', 'dockerhub')
    def dockerRegistry = parameters.get('dockerRegisty', 'docker.io')
    def branch = parameters.get('branch', 'master')

    def pom = readMavenPom file: 'pom.xml'
    def groupId = pom.groupId
    def version = pom.version

    sh "git checkout ${branch}"

    //Perform traditional release and deploy to sonatype
    sh 'mvn -B release:clean release:prepare release:perform'

    //List all repositories and grab the output.
    def stagingRepositoryPrefix = groupId.replaceAll("\\.","")
    def stagingRepositoryId = sh(returnStdout: true,
                                 script: "mvn org.sonatype.plugins:nexus-staging-maven-plugin:${nexusStagingPluginVersion}:rc-list -DnexusUrl=${nexusUrl} -DserverId=${nexusServerId} | grep ${stagingRepositoryPrefix} | head -n 1 | awk -F \" \" '{print \$2}'"
    ).trim()

    //Close the repository
    sh "mvn org.sonatype.plugins:nexus-staging-maven-plugin:${nexusStagingPluginVersion}:rc-close -DnexusUrl=${nexusUrl} -DserverId=${nexusServerId} -DstagingRepositoryId=${stagingRepositoryId}"

    //Release the repository
    sh "mvn org.sonatype.plugins:nexus-staging-maven-plugin:${nexusStagingPluginVersion}:rc-release -DnexusUrl=${nexusUrl} -DserverId=${nexusServerId} -DstagingRepositoryId=${stagingRepositoryId}"

    sh "git push origin ${branch}"
    sh "git push origin --tags"

    return getLatestMavenReleaseVersion()
}
