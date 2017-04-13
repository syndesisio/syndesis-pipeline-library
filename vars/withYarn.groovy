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
    def yarnImage = parameters.get('yarnImage', 'rhipaas/karma-xvfb:latest')
    def inheritFrom = parameters.get('inheritFrom', 'base')
    def serviceAccount = parameters.get('serviceAccount', '')

    def alwaysPullImage = yarnImage.endsWith(":latest")

    podTemplate(cloud: "${cloud}", name: "${name}", label: label, inheritFrom: "${inheritFrom}", serviceAccount: "${serviceAccount}",
            containers: [
                    containerTemplate(
                            name: 'yarn', image: "${yarnImage}",
                            envVars: [
                                    containerEnvVar(key: 'LD_PRELOAD',value: 'libnss_wrapper.so'),
                                    containerEnvVar(key: 'NSS_WRAPPER_PASSWD',value: '/tmp/passwd'),
                                    containerEnvVar(key: 'NSS_WRAPPER_GROUP', value: '/etc/group')
                            ],
                            //We use chkpasswd to generate the passwd file required by git...
                            command: '/usr/local/bin/chkpasswd', args: 'cat', ttyEnabled: true,
                            alwaysPullImage: alwaysPullImage)]) {
        body()
    }
}

