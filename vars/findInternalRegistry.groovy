#!/usr/bin/groovy

/**
 * Returns the id of the build, which consists of the job name, build number and an optional prefix.
 * @param prefix    The prefix to use, defaults in empty string.
 * @return
 */
def call(imagestream = '') {
     try {
      return  sh(returnStdout: true, script: "oc get is " + imagestream + " | awk -F ' ' '{print \$2}' | awk -F '/' '{print \$1}' | grep -vi docker | head -n 1").trim()
     } catch (Throwable t) {
      return ''
     }
}
