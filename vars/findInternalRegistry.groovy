#!/usr/bin/groovy

/**
 * Returns the id of the build, which consists of the job name, build number and an optional prefix.
 * @param prefix    The prefix to use, defaults in empty string.
 * @return
 */
def call(parameters = [:]) {
  def currentNamespace = "${env.KUBERNETES_NAMESPACE}"
  if (currentNamespace.isEmpty()) {
    currentNamespace= 'syndesis-ci'
  }

  def imagestream = parameters.get('imagestream', '')
  def namespace = parameters.get('namespace', currentNamespace)
  try {
    return sh(returnStdout: true, script: "oc get is ${imagestream} -n ${namespace} | awk -F ' ' '{print \$2}' | awk -F '/' '{print \$1}' | grep -vi docker | head -n 1").trim()
  } catch (Throwable t) {
    return ''
  }
}
