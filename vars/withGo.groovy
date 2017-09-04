#!/usr/bin/groovy

/**
 * Wraps the code in a podTemplate with the Go container.
 * @param parameters Parameters to customize the Go container.
 * @param body The code to wrap.
 * @return
 */
def call(Map parameters = [:], body) {

    def defaultLabel = buildId('go')
    def label = parameters.get('label', defaultLabel)
    def name = parameters.get('name', 'go')

    def cloud = parameters.get('cloud', 'openshift')
    def goImage = parameters.get('goImage', 'syndesis/go-18-centos7:latest')

    def envVars = parameters.get('envVars', [])
    def inheritFrom = parameters.get('inheritFrom', 'base')
    def namespace = parameters.get('namespace', 'syndesis-ci')
    def serviceAccount = parameters.get('serviceAccount', '')
    def idleMinutes = parameters.get('idle', 10)

    def alwaysPullImage = goImage.endsWith(":latest")

    envVars.add(containerEnvVar(key: 'LD_PRELOAD',value: 'libnss_wrapper.so'))
    envVars.add(containerEnvVar(key: 'NSS_WRAPPER_PASSWD',value: '/tmp/passwd'))
    envVars.add(containerEnvVar(key: 'NSS_WRAPPER_GROUP', value: '/etc/group'))

    podTemplate(
        cloud: "${cloud}",
        name: "${name}",
        namespace: "${namespace}",
        label: label,
        inheritFrom: "${inheritFrom}",
        serviceAccount: "${serviceAccount}",
        idleMinutesStr: "${idleMinutes}",
        containers: [
            containerTemplate(
                name: 'go',
                image: "${goImage}",
                command: '/usr/local/bin/chkpasswd',
                args: 'cat',
                ttyEnabled: true,
                envVars: envVars,
                alwaysPullImage: alwaysPullImage
            )
        ],
    ) {
        body()
    }
}

