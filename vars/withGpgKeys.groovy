#!/usr/bin/groovy

/**
 * Wraps the code in a podTemplate with the gpg-keys-provider container.
 * The template defines the secret volume and mount for mounting gpg keys.
 * Also it defines an init container that sets the right permissions.
 * @param parameters    Parameters to customize the template.
 * @param body          The code to wrap.
 * @return
 */
def call(Map parameters = [:], body) {
    def defaultLabel = buildId('gpg-keys-provider')
    def label = parameters.get('label', defaultLabel)
    def name = parameters.get('name', 'gpg-keys-provider')

    def cloud = parameters.get('cloud', 'openshift')
    def inheritFrom = parameters.get('inheritFrom', 'base')
    def namespace = parameters.get('namespace', 'syndesis-ci')
    def image = parameters.get('image', 'centos:centos7') //this needs to be libc based and not musl based image, since it depends on withArbitraryUser {}.
    def home = parameters.get('home', '/home/jenkins')
    def gpgKeysSecret = parameters.get('gpgKeysSecret', 'gpg-keys')

    podTemplate(cloud: "${cloud}", name: "${name}", namespace: "${namespace}",
                volumes: [secretVolume(secretName: "${gpgKeysSecret}", mountPath: "/usr/local/share/gpg-keys")],
                initContainers: [containerTemplate(name: "${name}",
                                                   image: "${image}",
                                                   command: "/bin/sh -c",
                                                   args: "\"mkdir ${home}/.gnupg && chmod 700 ${home}/.gnupg && gpg --homedir=${home}/.gnupg --list-public-keys && gpg --homedir=${home}/.gnupg --import /usr/local/share/gpg-keys/public.key && gpg --homedir=${home}/.gnupg --import /usr/local/share/gpg-keys/private.key\"")]) {
        body()
    }
}
