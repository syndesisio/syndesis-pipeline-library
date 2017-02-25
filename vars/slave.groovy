#!/usr/bin/groovy

def call(Map parameters = [:], body) {

    def defaultLabel = "jnlp.${env.JOB_NAME}.${env.BUILD_NUMBER}".replaceAll('-', '_').replaceAll('/', '_').replaceAll(' ', '_')
    def label = parameters.get('label', defaultLabel)

    def jnlpImage = parameters.get('jnlpImage', 'openshift/jenkins-slave-maven-centos7')
    def inheritFrom = parameters.get('inheritFrom', 'base')
    def serviceAccount = parameters.get('serviceAccount', '')

    podTemplate(label: label, inheritFrom: "${inheritFrom}", serviceAccount: "${serviceAccount}",
            containers: [containerTemplate(name: 'jnlp', image: "${jnlpImage}", args: '${computer.jnlpmac} ${computer.name}')]) {
        body()
    }
}

