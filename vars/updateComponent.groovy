#!/usr/bin/groovy

def call(Map parameters = [:]) {

    def component = parameters.get('component', 'unknown')
    def version = parameters.get('version', '1.0')
    def namespace = parameters.get('namespace', 'default')
    def key = "${component}_VERSION".toUpper().replace('-', '_')
    
     container(name: 'openshift') {
        sh "oc process redhat-ipaas-dev-single-tenant -l component=${component} \
                -p OPENSHIFT_MASTER=${namespace} \
                -p ROUTE_HOSTNAME=ipaas-staging.b6ff.rh-idev.openshiftapps.com \
                -p KEYCLOAK_ROUTE_HOSTNAME=ipaas-staging-keycloak.b6ff.rh-idev.openshiftapps.com \
                -p OPENSHIFT_OAUTH_CLIENT_ID=system:serviceaccount:${namespace}:ipaas-oauth-client \
                -p OPENSHIFT_OAUTH_CLIENT_SECRET=\$(oc sa get-token ipaas-oauth-client -n ${namespace}) \
                -p OPENSHIFT_OAUTH_DEFAULT_SCOPES=\"user:info user:check-access role:edit:${namespace}:!\" \
                -p ${key}=${version} | oc update -f -n ${namespace}"
        }
}
