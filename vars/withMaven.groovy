#!/usr/bin/groovy

/**
 * Wraps the code in a podTemplate with the Maven container.
 * @param parameters Parameters to customize the Maven container.
 * @param body The code to wrap.
 * @return
 */
def call(Map parameters = [:], body) {

    def defaultLabel = buildId('maven')
    def label = parameters.get('label', defaultLabel)
    def name = parameters.get('name', 'maven')

    def cloud = parameters.get('cloud', 'openshift')

    def envVars = parameters.get('envVars', [])
    def inheritFrom = parameters.get('inheritFrom', 'base')
    def namespace = parameters.get('namespace', 'syndesis-ci')
    def serviceAccount = parameters.get('serviceAccount', '')
    def workingDir = parameters.get('workingDir', '/home/jenkins')
    def mavenRepositoryClaim = parameters.get('mavenRepositoryClaim', '')
    def mavenSettingsXmlSecret = parameters.get('mavenSettingsXmlSecret', '')
    def mavenLocalRepositoryPath = parameters.get('mavenLocalRepositoryPath', "${workingDir}/.m2/repository/")
    def mavenSettingsXmlMountPath = parameters.get('mavenSettingsXmlMountPath', "${workingDir}/.m2")
    def idleMinutes = parameters.get('idle', 10)

    def isPersistent = !mavenRepositoryClaim.isEmpty()
    def hasSettingsXml = !mavenSettingsXmlSecret.isEmpty()

    def internalRegistry = parameters.get('internalRegistry', findInternalRegistry(namespace: "$namespace", imagestream: "maven-with-repo"))
    def mavenImage = !internalRegistry.isEmpty() ? parameters.get('mavenImage', "${internalRegistry}/${namespace}/maven-with-repo:latest") : parameters.get('mavenImage', 'maven:3.5.0')

    def volumes = []
    envVars.add(containerEnvVar(key: 'MAVEN_OPTS', value: "-Duser.home=${workingDir} -Dmaven.repo.local=${mavenLocalRepositoryPath}"))

    if (isPersistent) {
        volumes.add(persistentVolumeClaim(claimName: "${mavenRepositoryClaim}", mountPath: "${mavenLocalRepositoryPath}"))
    } else {
        volumes.add(emptyDirVolume(mountPath: "${mavenLocalRepositoryPath}"))
    }

    if (hasSettingsXml) {
        volumes.add(secretVolume(secretName: "${mavenSettingsXmlSecret}", mountPath: "${mavenSettingsXmlMountPath}"))
    }

    podTemplate(cloud: "${cloud}", name: "${name}", namespace: "${namespace}", label: label, inheritFrom: "${inheritFrom}", serviceAccount: "${serviceAccount}",
            idleMinutesStr: "${idleMinutes}",
            containers: [containerTemplate(name: 'maven', image: "${mavenImage}", command: '/bin/sh -c', args: 'cat', ttyEnabled: true, envVars: envVars)],
            volumes: volumes) {
        body()
    }
}

