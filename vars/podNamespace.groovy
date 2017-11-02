#!/usr/bin/groovy

/**
 * Returns the namespace of the current pod.
 * @return
 */
def call() {
    return sh(returnStdout: true, script: 'cat /var/run/secrets/kubernetes.io/serviceaccount/namespace').trim()
}
