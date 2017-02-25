#!/usr/bin/groovy

def call(Map parameters = [:], body) {

    def defaultLabel = "slave.${env.JOB_NAME}.${env.BUILD_NUMBER}".replace('-', '_').replace('/', '_').replaceAll(' ', '_')
    def label = parameters.get('label', defaultLabel)

    slave(parameters) {
        node(label) {
            body()
        }
    }
}
