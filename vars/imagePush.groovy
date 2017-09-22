#!/bin/groovy

def call(Map parameters = [:]) {

    def name = parameters.get('name', '')

    def registry = parameters.get('registry', 'docker.io')
    def user = parameters.get('user', 'syndesis')
    def repo = parameters.get('repo', '')
    def tag = parameters.get('tag', 'latest')

    def pushSecret = parameters.get('pushSecret', 'dockerhub')

    def imageStreamTag = parameters.get('imageStreamTag', '')
    def dockerImage = parameters.get('dockerImage', '')

    def sourceImageKind = 'ImageStreamTag'
    def sourceImageName = imageStreamTag

    if (imageStreamTag.isEmpty() && !dockerImage.isEmpty()) {
        sourceImageKind = 'DockerImage'
        sourceImageName = dockerImage
    }

    def yaml =
"apiVersion: v1\n" +
"kind: BuildConfig\n" +
"metadata:\n" +
"  labels:\n" +
"    app: ${name}\n" +
"  name: ${name}\n" +
"spec:\n" +
"  source:\n" +
"    type: Dockerfile\n" +
"    dockerfile: \"FROM thisisgettingreplaced\"\n" +
"  output:\n" +
"    to:\n" +
"      kind: DockerImage\n" +
"      name: ${registry}/${user}/${repo}:${tag}\n" +
"    pushSecret:\n" +
"      name: ${pushSecret}\n" +
"  strategy:\n" +
"    dockerStrategy:\n" +
"      from:\n" +
"        kind: ${sourceImageKind}\n" +
"        name: ${sourceImageName}\n" +
"    type: Docker\n"


    openshiftCreateResource(jsonyaml: "${yaml}")
    openshiftBuild(bldCfg: "${name}")
}
