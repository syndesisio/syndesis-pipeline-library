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
    def mavenImage = parameters.get('mavenImage', 'maven:3.3.9')
    def inheritFrom = parameters.get('inheritFrom', 'base')
    def serviceAccount = parameters.get('serviceAccount', '')
    def workingDir = parameters.get('workingDir', '/home/jenkins')
    def mavenRepositoryClaim = parameters.get('mavenRepositoryClaim', '')
    def mavenSettingsXmlSecret = parameters.get('mavenSettingsXmlSecret', '')
    def mavenSettingsXmlMountPath = parameters.get('mavenSettingsXmlMountPath', "/${workingDir}/.m2/")

    def isPersistent = !mavenRepositoryClaim.isEmpty()
    def hasSettingsXml = !mavenSettingsXmlSecret.isEmpty()

    def volumes = []

    if (isPersistent) {
        volumes.add(persistentVolumeClaim(claimName: "${mavenRepositoryClaim}", mountPath: "/${workingDir}/.m2/repository"))
    }

    if (hasSettingsXml) {
        volumes.add(secretVolume(secretName: "${mavenSettingsXmlSecret}", mountPath: "${mavenSettingsXmlMountPath}"))
    }

    podTemplate(cloud: "${cloud}", name: "${name}", label: label, inheritFrom: "${inheritFrom}", serviceAccount: "${serviceAccount}",
            containers: [containerTemplate(name: 'maven', image: "${mavenImage}", command: '/bin/sh -c', args: 'cat', ttyEnabled: true, envVars: [containerEnvVar(key: 'MAVEN_OPTS', value: "-Duser.home=${workingDir} -Dmaven.repo.local=${workingDir}/.m2/repository/")])],
            volumes: volumes) {
        body()
    }
}

