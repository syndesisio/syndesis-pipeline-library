def cases = [
    [
        mavenSettings: [mavenImage: "openjdk:8", serviceAccount: "builder", mavenSettingsXmlSecret: 'm2-settings']
    ], [
        mavenSettings: [mavenImage: "openjdk:8", serviceAccount: "builder"]
    ]
]

node {
    checkout scm
    library identifier: "syndesis-pipeline-library@${env.BRANCH_NAME}", retriever: workspaceRetriever("${WORKSPACE}")
    inNamespace(cloud:'openshift', prefix: 'e2e') {

        stage('Prepare Environment') {
            createEnvironment(
                cloud: 'openshift', name: "${KUBERNETES_NAMESPACE}",
                environmentSetupScriptUrl: 'https://raw.githubusercontent.com/syndesisio/syndesis-system-tests/master/src/test/resources/setup.sh',
                environmentTeardownScriptUrl: 'https://raw.githubusercontent.com/syndesisio/syndesis-system-tests/master/src/test/resources/teardown.sh',
                waitForServiceList: ['syndesis-rest', 'syndesis-ui', 'syndesis-keycloak', 'syndesis-verifier'],
                waitTimeout: 600000L,
                namespaceDestroyEnabled: false,
                namespaceCleanupEnabled: false
            )
        }

        for (i = 0; i < cases.size; i++) {
            stage("Building [case:$i]") {
                slave {
                    withOpenshift {
                        withMaven(cases[i].mavenSettings) {
                            inside {
                                stage("System Tests [case:$i]") {
                                    def testingNamespace = currentNamespace()
                                    test(component: 'syndesis-pipeline-library', envInitEnabled: false, namespace: "${KUBERNETES_NAMESPACE}", serviceAccount: 'jenkins')
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

