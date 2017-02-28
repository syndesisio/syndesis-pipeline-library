#!/usr/bin/groovy

def call(Map parameters = [:]) {

    def component = parameters.get('component')
    def version = parameters.get('version', '1.0')
    def key = "${component}_VERSION".toUpperCase().replace('-', '_')
    def defaultNamespace = "systest-${version}"
    def namespace = parameters.get('namespace', defaultNamespace)
    def parametersFilePath = "parameters.yml";
    
    container(name: 'openshift') {
            parametersFilePath = pwd() + "/parameters.yml"
            git 'https://github.com/redhat-ipaas/ipaas-system-tests.git'

            sh "oc create -f https://raw.githubusercontent.com/redhat-ipaas/openshift-templates/master/serviceaccount-as-oauthclient-single-tenant.yml -n ${namespace}"

            sh "echo OPENSHIFT_MASTER: \$(oc whoami --show-server) > ${parametersFilePath}"
            sh "echo ROUTE_HOSTNAME: ipaas-staging.b6ff.rh-idev.openshiftapps.com >> ${parametersFilePath}"
            sh "echo KEYCLOAK_ROUTE_HOSTNAME: ipaas-staging-keycloak.b6ff.rh-idev.openshiftapps.com >> ${parametersFilePath}"
            sh "echo OPENSHIFT_OAUTH_CLIENT_ID: system:serviceaccount:${namespace}:ipaas-oauth-client >> ${parametersFilePath}"
            sh "echo OPENSHIFT_OAUTH_CLIENT_SECRET: \$(oc sa get-token ipaas-oauth-client -n ${namespace}) >> ${parametersFilePath}"
            sh "echo OPENSHIFT_OAUTH_DEFAULT_SCOPES: \"user:info user:check-access role:edit:${namespace}:!\" >> ${parametersFilePath}"
            sh "echo $key:  $version >> ${parametersFilePath}"
        }

        container(name: 'maven') {
            sh "cat ${parametersFilePath}"
            sh "mvn clean install -U -Dtemplate.parameters.file=${parametersFilePath} -Dnamespace.use.existing=${namespace}"
        }
}
