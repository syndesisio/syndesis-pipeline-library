# Syndesis Pipeline Library

A pipeline library based on the [Fabric8 Pipeline Library](https://github.com/fabric8io/fabric8-pipeline-library) with limited scope and slower pace.

## Overview

The pipeline library is supposed to make building, testing an rolling out stuff on Openshift as easy as possible:

    slave {
        withOpenshift {
                withMaven {
                    inside {
                        def testingNamespace = generateProjectName()
                        checkout scm
    
                        stage 'Build'
                        container(name: 'maven') {
                            sh "mvn clean install
                        }
    
                        stage 'System Tests'
                        test(component: 'my-component')
    
                        stage 'Rollout'
                        tag(sourceProject: 'ci', imageStream: 'my-component')
                        rollout(deploymentConfig: 'my-component', namespace: 'staging')
                     }
            }
        }
    }


## Functions

### slave

A shortcut to a `podTemplate` that defines a Jenkins slave.

Can accept the following named arguments:

- **cloud** The configured Jenkins cloud (defaults to Openshift)
- **jnlpImage** The image of the jnlp container to use
- **serviceAccount** The serviceAccount

### withMaven

A shortcut to a `podTemplate` that defines a maven container

Can accept the following named arguments:

- **cloud** The configured Jenkins cloud (defaults to Openshift)
- **mavenImage** The image of the maven container to use
- **serviceAccount** The serviceAccount

### withOpenshift

A shortcut to a `podTemplate` that defines a Openshift container *(a container with the oc)*

Can accept the following named arguments:

- **cloud** The configured Jenkins cloud (defaults to Openshift)
- **openshiftImage** The image of the maven container to use
- **serviceAccount** The serviceAccount

### inside

Creates the actual pod based on the parent `podTemplate`.
 
### test


Runs the Syndesis system tests suite.

Can accept the following named arguments:

- **namespace** The namespace in which the tests are supposed to run.

### tag

Tags an imagestream.

Can accept the following named arguments:

- **imageStream** The imageStream.
- **tag** The tag.
- **sourceProject** The source project/namespace.
- **targetProject** The taget project/namespace.
- **sourceImageStream** The source ImageStream (if not the same as target).
- **targetImageStream** The target ImageStream (if not the same as source).
- **sourceTag** The source tag (if not the same as target).
- **targetTag** The target tag (if not the same as source).

### rollout

Rolls out the changes to the target project.
Can accept the following named arguments:

- **deploymentConfig** The deployment config to rollout
- **namespace** The namespace to rollout changes to.