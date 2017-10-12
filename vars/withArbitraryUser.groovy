#!/usr/bin/groovy

/**
 * Wraps the code in a podTemplate with the Nsswrapper container.
 * @param parameters    Parameters to customize the Nsswrapper container.
 * @param body          The code to wrap.
 * @return
 */
def call(Map parameters = [:], body) {


    def defaultLabel = buildId('nsswrapper-provider')
    def label = parameters.get('label', defaultLabel)
    def name = parameters.get('name', 'nsswrapper-provider')

    def cloud = parameters.get('cloud', 'openshift')
    def image = parameters.get('image', 'syndesis/nsswrapper')
    def inheritFrom = parameters.get('inheritFrom', 'base')
    def namespace = parameters.get('namespace', 'syndesis-ci')
    def username = parameters.get('username', 'jenkins')
    def group = parameters.get('group', 'root')
    def description = parameters.get('description', 'Jenkins User')
    def home = parameters.get('home', '/home/jenkins')
    def nssDir = parameters.get('nssDir', '/home/jenkins')

    podTemplate(cloud: "${cloud}", name: "${name}",
                envVars: [podEnvVar(key: 'LD_PRELOAD', value: "${nssDir}/libnss_wrapper.so"),
                          podEnvVar(key: 'NSS_DIR', value: "${nssDir}"),
                          podEnvVar(key: 'NSS_WRAPPER_PASSWD', value: "${nssDir}/build.passwd"),
                          podEnvVar(key: 'NSS_WRAPPER_GROUP', value: '/etc/group'),
                          podEnvVar(key: 'NSS_USER_NAME', value: "${username}"),
                          podEnvVar(key: 'NSS_USER_DESCRIPTION', value: "${description}"),
                          podEnvVar(key: 'NSS_USER_HOME', value: "${home}"),
        ],
                initContainers: [containerTemplate(name: "${name}",
                                                   image: 'syndesis/nsswrapper',
                                                   command: '/usr/local/bin/init.sh')]) {
        body()
    }
}
