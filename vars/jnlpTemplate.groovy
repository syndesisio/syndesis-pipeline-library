#!/usr/bin/groovy

def call(Map parameters = [:], body) {

    def defaultLabel = "jnlp.${env.JOB_NAME}.${env.BUILD_NUMBER}".replace('-', '_').replace('/', '_')
    def label = parameters.get('label', defaultLabel)

    def jnlpImage = parameters.get('jnlpImage', 'openshift/jenkins-slave-jnlp-centos7')
    def inheritFrom = parameters.get('inheritFrom', 'base')
    podTemplate(label: label, inheritFrom: "${inheritFrom}",
            containers: [containerTemplate(name: 'jnlp', image: "${jnlpImage}", args: '${computer.jnlpmac} ${computer.name}')]) {
        body()
    }
}

