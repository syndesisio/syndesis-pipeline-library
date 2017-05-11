node {
    checkout scm
    library identifier: "syndesis-pipeline-library@${env.BRANCH_NAME}", retriever: workspaceRetriever("${WORKSPACE}")
    def mavenVersion='3.3.9'

    slave {
        withOpenshift {
                //Comment out until pvc issues are resolved
                //withMaven(mavenImage: "maven:${mavenVersion}", serviceAccount: "jenkins", mavenRepositoryClaim: "m2-local-repo", mavenSettingsXmlSecret: 'm2-settings') {
                  withMaven(mavenImage: "maven:${mavenVersion}", serviceAccount: "jenkins", mavenSettingsXmlSecret: 'm2-settings') {
                    inside {
                        stage 'System Tests'
                        def testingNamespace = generateProjectName()
                        test(component: 'ipaas-rest', namespace: "${testingNamespace}", serviceAccount: 'jenkins')
                     }

            }
        }
    }
}