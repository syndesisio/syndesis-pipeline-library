#!/usr/bin/groovy

def call(Map parameters = [:]) {
    def prefix = parameters.get('prefix', '')
    return  "${prefix}${env.JOB_NAME}.${env.BUILD_NUMBER}".replaceAll('-', '_').replaceAll('/', '_').replaceAll(' ', '_')
}