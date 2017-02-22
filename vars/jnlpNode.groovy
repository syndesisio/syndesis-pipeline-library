#!/usr/bin/groovy

def call(Map parameters = [:], body) {

    def defaultLabel = "jnlp.${env.JOB_NAME}.${env.BUILD_NUMBER}".replace('-', '_').replace('/', '_')
    def label = parameters.get('label', defaultLabel)

    jnlpTemplate(parameters) {
        node(label) {
            body()
        }
    }
}
