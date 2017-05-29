#!/usr/bin/groovy

/**
 * Wraps the code in a podTemplate with the yarn container.
 * @param parameters    Parameters to customize the yarn container.
 * @param body          The code to wrap.
 * @return
 */
def call(Map parameters = [:], body) {


    def defaultLabel = buildId('yarn')
    def label = parameters.get('label', defaultLabel)
    def name = parameters.get('name', 'yarn')

    def cloud = parameters.get('cloud', 'openshift')
    def yarnImage = parameters.get('yarnImage', 'syndesis/karma-xvfb:latest')

    def envVars = parameters.get('envVars', [])
    def namespace = parameters.get('namespace', 'syndesis-ci')
    def inheritFrom = parameters.get('inheritFrom', 'base')
    def serviceAccount = parameters.get('serviceAccount', '')
    def idleMinutes = parameters.get('idle', 10)

    def alwaysPullImage = yarnImage.endsWith(":latest")

    envVars.add(containerEnvVar(key: 'LD_PRELOAD',value: 'libnss_wrapper.so'))
    envVars.add(containerEnvVar(key: 'NSS_WRAPPER_PASSWD',value: '/tmp/passwd'))
    envVars.add(containerEnvVar(key: 'NSS_WRAPPER_GROUP', value: '/etc/group'))

    podTemplate(cloud: "${cloud}", name: "${name}", namespace: "${namespace}", label: label, inheritFrom: "${inheritFrom}", serviceAccount: "${serviceAccount}",
            idleMinutesStr: "${idleMinutes}",
            containers: [
                    containerTemplate(
                            name: 'yarn', image: "${yarnImage}",
                            envVars: envVars,
                            //We use chkpasswd to generate the passwd file required by git...
                            command: '/usr/local/bin/chkpasswd', args: 'cat', ttyEnabled: true,
                            alwaysPullImage: alwaysPullImage)]) {
        body()
    }
}

