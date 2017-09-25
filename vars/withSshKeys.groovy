#!/usr/bin/groovy

/**
 * Wraps the code in a podTemplate with the ssh-keys-provider container.
 * The template defines the secret volume and mount for mounting ssh keys.
 * Also it defines an init container that sets the right permissions.
 * @param parameters    Parameters to customize the template.
 * @param body          The code to wrap.
 * @return
 */
def call(Map parameters = [:], body) {
    def defaultLabel = buildId('nsswrapper')
    def label = parameters.get('label', defaultLabel)
    def name = parameters.get('name', 'ssh-keys-provider')

    def cloud = parameters.get('cloud', 'openshift')
    def inheritFrom = parameters.get('inheritFrom', 'base')
    def namespace = parameters.get('namespace', 'syndesis-ci')
    def home = parameters.get('home', '/home/jenkins')
    def sshKeysSecret = parameters.get('sshKeysSecret', 'ssh-keys')

    podTemplate(cloud: "${cloud}", name: "${name}", namespace: "${namespace}",
                volumes: [secretVolume(secretName: "${sshKeysSecret}", mountPath: "/usr/local/share/ssh-keys")],
                initContainers: [containerTemplate(name: "${name}",
                                                   image: 'busybox',
                                                   command: "/bin/sh -c",
                                                   args: "\"mkdir ${home}/.ssh && chmod 700 ${home}/.ssh && cp /usr/local/share/ssh-keys/* ${home}/.ssh/ && chmod 600 ${home}/.ssh/*\"")]) {
        body()
    }
}
