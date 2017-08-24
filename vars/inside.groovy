#!/usr/bin/groovy

/**
 * Wraps the code in a slave container.
 * @param parameters
 * @param body
 * @return
 */
def call(Map parameters = [:], body) {

    def defaultLabel = buildId('slave')
    def label = parameters.get('label', defaultLabel)

    slave(parameters) {
        node(label) {
            sh '''
                if [ -d $HOME/.m2-ro ]; then
                    mkdir -p $HOME/.m2 && cp -vf $HOME/.m2-ro/* $HOME/.m2/
                fi
            '''
            body()
        }
    }
}
