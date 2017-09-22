#!/usr/bin/groovy

/**
 * Returns the latest release number using the git log.
 * @param parameters    release parameters.
 * @return
 */
def call(Map parameters = [:]) {
    def pom = readMavenPom file: 'pom.xml'
    def artifactId = pom.artifactId

    return sh(returnStdout: true,
                   script:  "git log | grep \"prepare release $artifactId-\" | head -n 1 | awk -F \"$artifactId-\" '{print \$2}'" ).trim()
}
