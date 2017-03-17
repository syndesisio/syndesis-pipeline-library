#!/usr/bin/groovy

/**
 * Wraps the code in a podTemplate with the Maven container.
 * @param parameters    Parameters to customize the Maven container.
 * @param body          The code to wrap.
 * @return
 */
def call(Map parameters = [:], body) {

    def defaultLabel = buildId('maven')
    def label = parameters.get('label', defaultLabel)

    def cloud = parameters.get('cloud', 'openshift')
    def mavenImage = parameters.get('mavenImage', 'maven:3.3.9')
    def inheritFrom = parameters.get('inheritFrom', 'base')
    def serviceAccount = parameters.get('serviceAccount', '')
    def workingDir = parameters.get('workingDir', '/home/jenkins')
    def mavenRepositoryClaim = parameters.get('mavenRepositoryClaim', '')
    def persistent = !mavenRepositoryClaim.isEmpty()

    if (persistent) {
        podTemplate(cloud: "${cloud}", label: label, inheritFrom: "${inheritFrom}", serviceAccount: "${serviceAccount}",
                containers: [containerTemplate(name: 'maven', image: "${mavenImage}", command: '/bin/sh -c', args: 'cat', ttyEnabled: true, envVars: [containerEnvVar(key: 'MAVEN_OPTS', value: "-Duser.home=${workingDir} -Dmaven.repo.local=${workingDir}/.m2/repository/")])],
                volumes: [persistentVolumeClaim(claimName: "${mavenRepositoryClaim}", mountPath: "/${workingDir}/.m2/repository")]) {
            body()
        }
    } else {
        podTemplate(cloud: "${cloud}", label: label, inheritFrom: "${inheritFrom}", serviceAccount: "${serviceAccount}",
                containers: [containerTemplate(name: 'maven', image: "${mavenImage}", command: '/bin/sh -c', args: 'cat', ttyEnabled: true, envVars: [containerEnvVar(key: 'MAVEN_OPTS', value: "-Duser.home=${workingDir} -Dmaven.repo.local=${workingDir}/.m2/repository/")])]) {
            body()
        }
    }
}

