#!/usr/bin/groovy

/**
 * Deploys the latest version of the deploymentconfig
 * @param parameters
 * @return
 */
def call(Map parameters = [:]) {

    def deploymentConfig = parameters.get('deploymentConfig', 'unknown')
    def namespace = parameters.get('namespace', 'ipaas-staging')


    container(name: 'openshift') {
        sh "oc rollout latest ${deploymentConfig} -n ${namespace}"
    }
}