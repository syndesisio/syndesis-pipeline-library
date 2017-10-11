node {

  checkout scm

  library identifier: "syndesis-pipeline-library@${env.BRANCH_NAME}", retriever: workspaceRetriever("${WORKSPACE}")

  def mavenVersion = '3.5.0'

  inNamespace(cloud: 'openshift', prefix: 'e2e') {

    slave {
      withOpenshift {
        withMaven(
          mavenImage: "maven:${mavenVersion}",
          serviceAccount: "jenkins"
        ) {
          inside {
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

            stage('System Tests') {
              test(
                component: 'syndesis-pipeline-library',
                envInitEnabled: false,
                namespaceDestroyEnabled: false, //We don't want to delete the namespace here. It will get wiped by the inNamespace block.
                namespace: "${KUBERNETES_NAMESPACE}",
                serviceAccount: 'jenkins'
              )
            }
          }
        }
      }
    }
  }
}
