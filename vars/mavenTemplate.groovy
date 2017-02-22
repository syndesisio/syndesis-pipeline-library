#!/usr/bin/groovy

def call(Map parameters = [:], body) {

    def defaultLabel = "maven.${env.JOB_NAME}.${env.BUILD_NUMBER}".replace('-', '_').replace('/', '_')
    def label = parameters.get('label', defaultLabel)

    def mavenImage = parameters.get('mavenImage', 'maven:3.3.9')
    def inheritFrom = parameters.get('inheritFrom', 'base')
    podTemplate(label: label, inheritFrom: "${inheritFrom}",
            containers: [containerTemplate(name: 'maven', image: "${mavenImage}", command: '/bin/sh -c', args: 'cat', ttyEnabled: true)]) {
        body()
    }
}

