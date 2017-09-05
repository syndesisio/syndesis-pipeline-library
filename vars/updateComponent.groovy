#!/usr/bin/groovy

def call(Map parameters = [:]) {

  def component = parameters.get('component', 'unknown')
  def version = parameters.get('version', '1.0')
  def namespace = parameters.get('namespace', 'default')
  def key = "${component}_VERSION".toUpper().replace('-', '_')

  container(name: 'openshift') {
    sh """
      oc process syndesisio-dev-single-tenant -l component=${component} \
            -p OPENSHIFT_MASTER=${namespace} \
            -p ROUTE_HOSTNAME=syndesis-staging.b6ff.rh-idev.openshiftapps.com \
            -p KEYCLOAK_ROUTE_HOSTNAME=syndesis-staging-keycloak.b6ff.rh-idev.openshiftapps.com \
            -p OPENSHIFT_OAUTH_CLIENT_ID=system:serviceaccount:${namespace}:syndesis-oauth-client \
            -p OPENSHIFT_OAUTH_CLIENT_SECRET=\$(oc sa get-token syndesis-oauth-client -n ${namespace}) \
            -p OPENSHIFT_OAUTH_DEFAULT_SCOPES=\"user:info user:check-access role:edit:${namespace}:!\" \
            -p ${key}=${version} | oc update -f -n ${namespace}
    """
  }
}
