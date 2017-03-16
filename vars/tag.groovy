#!/usr/bin/groovy

/**
 * Promotes an image from one project to the other
 * @param parameters
 * @return
 */
def call(Map parameters = [:]) {

    def sourceProject = parameters.get('sourceProject', 'ipaas-testing')
    def targetProject = parameters.get('targetProject', 'ipaas-staging')

    def imageStream = parameters.get('imageStream', 'unknown')
    def sourceImageStream = parameters.get('sourceImageStream', imageStream)
    def targetImageStream = parameters.get('targetImageStream', sourceImageStream)

    def tag = parameters.get('tag', 'latest')
    def sourceTag = parameters.get('sourceTag', tag)
    def targetTag = parameters.get('targetTag', tag)


    container(name: 'openshift') {
        sh "oc tag ${sourceProject}/${sourceImageStream}:${sourceTag} ${targetProject}/${targetImageStream}:${targetTag} -n ${sourceProject}"
    }
}