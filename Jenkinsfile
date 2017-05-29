node {
    checkout scm
    library identifier: "syndesis-pipeline-library@${env.BRANCH_NAME}", retriever: workspaceRetriever("${WORKSPACE}")
    def mavenVersion='3.3.9'
    cube.namespace().withCloud('openshift').withPrefix('e2e').inside {

        cube.environment().withCloud('openshift').withName("${KUBERNETES_NAMESPACE}")
                                    .withConfigUrl("file:${WORKSPACE}/manifests/jenkins-sa.yml")
                                    .withNamespaceDestroyEnabled(false)
                                    .withNamespaceCleanupEnabled(false)
                                    .withWaitTimeout(600000L)
                                    .create()
        env = []
        env.add(containerEnvVar(key:'NAMESPACE_USE_EXISTING', value: "${KUBERNETES_NAMESPACE}"))
        env.add(containerEnvVar(key:'NAMESPACE_DESTROY_ENABLED', value: "false"))
        env.add(containerEnvVar(key:'NAMESPACE_CLEANUP_ENABLED', value: "false"))
        env.add(containerEnvVar(key:'ENV_INIT_ENABLED', value: "false"))

        stage 'Building'
        slave {
            withOpenshift {
                    withMaven(mavenImage: "maven:${mavenVersion}",
                    envVar: env,
                    serviceAccount: "jenkins", mavenRepositoryClaim: "m2-local-repo", mavenSettingsXmlSecret: 'm2-settings') {
                        inside {
                                stage 'Prepare Environment'
                                cube.environment()
                                    .withCloud('openshift')
                                    .withName("${KUBERNETES_NAMESPACE}")
                                    .withSetupScriptUrl('https://raw.githubusercontent.com/syndesisio/syndesis-system-tests/master/src/test/resources/setup.sh')
                                    .withTeardownScriptUrl('https://raw.githubusercontent.com/syndesisio/syndesis-system-tests/master/src/test/resources/teardown.sh')
                                    .withServicesToWait(['syndesis-rest', 'syndesis-ui', 'syndesis-keycloak', 'syndesis-verifier'])
                                    .withWaitTimeout(600000L)
                                    .withNamespaceDestroyEnabled(false)
                                    .withNamespaceCleanupEnabled(false)
                                    .create()

                                stage 'System Tests'
                                def testingNamespace = cube.getCurrentNamespace()
                                test(component: 'syndesis-pipeline-library', namespace: "${KUBERNETES_NAMESPACE}", serviceAccount: 'jenkins')
                        }
                    }
            }
        }
    }
}