#!/usr/bin/groovy

def call(Map parameters = [:]) {

    def component = parameters.get('component', defaultLabel)
    def version = parameters.get('version', '1.0')
    def namespace = parameters.get('namespace', 'default')
    def key = "${component}_${version}".toUpper().replace('-', '_')
    
     container(name: 'openshift') {
        sh "oc process redhat-ipaas-dev-single-tenant -l component=${component} \
                -p OPENSHIFT_MASTER=${namespace} \
                -p OPENSHIFT_OAUTH_CLIENT_ID=system:serviceaccount:${namespace}:ipaas-oauth-client \
                -p OPENSHIFT_OAUTH_CLIENT_SECRET=\$(oc sa get-token ipaas-oauth-client) \
                -p OPENSHIFT_OAUTH_DEFAULT_SCOPES=\"user:info user:check-access role:edit:${namespace}:!\" \
                -p ${key}i=${version} | oc update -f -n ${namespace}"
        }
}
