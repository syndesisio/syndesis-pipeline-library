#!/usr/bin/groovy

def call(Map parameters = [:], body) {

    def defaultLabel = buildId(prefix : 'maven')
    def label = parameters.get('label', defaultLabel)

    def mavenImage = parameters.get('mavenImage', 'maven:3.3.9')
    def inheritFrom = parameters.get('inheritFrom', 'base')
    def serviceAccount = parameters.get('serviceAccount', '')
    def workingDir = parameters.get('workingDir', '/home/jenkins')
    def mavenRepositoryClaim = parameters.get('mavenRepositoryClaim', '')
    def persistent = !mavenRepositoryClaim.isEmpty()

    if (persistent) {
        podTemplate(label: label, inheritFrom: "${inheritFrom}", serviceAccount: "${serviceAccount}",
                containers: [containerTemplate(name: 'maven', image: "${mavenImage}", command: '/bin/sh -c', args: 'cat', ttyEnabled: true, envVars: [containerEnvVar(key: 'MAVEN_OPTS', value: "-Duser.home=${workingDir} -Dmaven.repo.local=${workingDir}/.mvnrepository/")])],
                volumes: [persistentVolumeClaim(claimName: "${mavenRepositoryClaim}", mountPath: "/${workingDir}/.mvnrepository")]) {
            body()
        }
    } else {
        podTemplate(label: label, inheritFrom: "${inheritFrom}", serviceAccount: "${serviceAccount}",
                containers: [containerTemplate(name: 'maven', image: "${mavenImage}", command: '/bin/sh -c', args: 'cat', ttyEnabled: true, envVars: [containerEnvVar(key: 'MAVEN_OPTS', value: "-Duser.home=${workingDir} -Dmaven.repo.local=${workingDir}/.mvnrepository/")])]) {
            body()
        }
    }
}

