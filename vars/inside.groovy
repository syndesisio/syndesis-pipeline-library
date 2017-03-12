#!/usr/bin/groovy

/**
 * Wraps teh code in a slave container.
 * @param parameters
 * @param body
 * @return
 */
def call(Map parameters = [:], body) {

    def defaultLabel = buildId('slave')
    def label = parameters.get('label', defaultLabel)

    slave(parameters) {
        node(label) {
            body()
        }
    }
}
