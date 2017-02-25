#!/usr/bin/groovy

def call(Map parameters = [:], body) {

    def defaultLabel = "maven.${env.JOB_NAME}.${env.BUILD_NUMBER}".replaceAll('-', '_').replaceAll('/', '_').replaceAll(' ', '_')
    def label = parameters.get('label', defaultLabel)

    def mavenImage = parameters.get('mavenImage', 'maven:3.3.9')
    def persistent = parameters.get('persistent', true)
    def inheritFrom = parameters.get('inheritFrom', 'base')
    def serviceAccount = parameters.get('serviceAccount', '')

    if (persistent) {
        podTemplate(label: label, inheritFrom: "${inheritFrom}", serviceAccount: "${serviceAccount}",
                containers: [containerTemplate(name: 'maven', image: "${mavenImage}", command: '/bin/sh -c', args: 'cat', ttyEnabled: true, envVars: [containerEnvVar(key: 'MAVEN_OPTS', value: '-Dmaven.repo.local=/root/.mvnrepository/')])],
                volumes: [persistentVolumeClaim(claimName: 'm2-local-repo', mountPath: '/root/.mvnrepository')]) {
            body()
        }
    } else {
        podTemplate(label: label, inheritFrom: "${inheritFrom}", serviceAccount: "${serviceAccount}",
                containers: [containerTemplate(name: 'maven', image: "${mavenImage}", command: '/bin/sh -c', args: 'cat', ttyEnabled: true)]) {
            body()
        }
    }
}

