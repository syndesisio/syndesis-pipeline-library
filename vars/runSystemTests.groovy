#!/usr/bin/groovy

def call(Map parameters = [:]) {

    def component = parameters.get('component')
    def version = parameters.get('version', '1.0')
    def key = "${component}_${version}".toUpperCase().replace('-', '_')
    
    container(name: 'openshift') {
            git 'https://github.com/redhat-ipaas/ipaas-system-tests.git'
            sh 'echo OPENSHIFT_MASTER: $(oc whoami --show-server) > parameters.yml'
            sh 'echo OPENSHIFT_OAUTH_CLIENT_ID: system:serviceaccount:$(oc project -q):ipaas-oauth-client >> parameters.yml'
            sh 'echo OPENSHIFT_OAUTH_CLIENT_SECRET: $(oc sa get-token ipaas-oauth-client) >> parameters.yml'
            sh 'echo OPENSHIFT_OAUTH_DEFAULT_SCOPES: "user:info user:check-access role:edit:$(oc project -q):!" >> parameters.yml'
            sh "echo $key:  $version >> parameters.yml"
            sh 'cat parameters.yml'
        }

        container(name: 'maven') {
            sh 'mvn clean install -Dtemplate.parameters.file=$(pwd)/parameters.yml'
        }
}
