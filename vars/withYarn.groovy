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
            containers: [containerTemplate(name: 'yarn', image: "${yarnImage}", command: '/bin/sh -c', args: '/usr/local/bin/chkpasswd cat', ttyEnabled: true, alwaysPullImage: alwaysPullImage,
                    envVars: [
                            containerEnvVar('LD_PRELOAD', '/usr/lib64/libnss_wrapper.so'),
                            containerEnvVar('NSS_WRAPPER_PASSWD', '/usr/local/share/passwd'),
                            containerEnvVar('NSS_WRAPPER_GROUP', '/etc/group'),
                    ]
            )]) {

        body()
    }
}

