#!/usr/bin/groovy

/**
 * Shares a binary present in a specific container with the rest of the pod, by copying it inside the workspace.
 * @param container     The container that has the binary.
 * @param binary        The binary.
 * @param workingDir    The workingDir defaults to ${HOME}
 * @return
 */
def call(String container, String binary, String workingDir = "\${HOME}") {

    container(name: "${container}") {
        sh """
        mkdir -p ${workingDir}/bin
        cp \$(which ${binary}) ${workingDir}/bin/
        """
    }
}

