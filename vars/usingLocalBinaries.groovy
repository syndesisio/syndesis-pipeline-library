#!/usr/bin/groovy

/**
 * Creates ${HOME}/bin and add its to the path.
 * @return
 */
def call(Map parameters = [:], body) {

    withEnv(['PATH+WHATEVER=${HOME}/bin']) {
        sh 'mkdir -p ${HOME}/bin'
        body()
    }
}

