#!/usr/bin/groovy

/**
 * Shares a binary present in a specific container with the rest of the pod, by copying it inside the workspace.
 * @param name     The container that has the binary.
 * @param binary        The binary.
 * @param workingDir    The workingDir defaults to ${HOME}
 * @return
 */
def call(String name, String binary, String workingDir = "\${HOME}") {


    if (name == null || name.isEmpty()) {
        throw new IllegalArgumentException("shareBinary requires a `name` parameter!")
    }

    if (binary == null || binary.isEmpty()) {
        throw new IllegalArgumentException("shareBinary requires a `binary` parameter!")
    }

    container(name: "${name}") {
        sh """
        mkdir -p ${workingDir}/bin
        cp \$(which ${binary}) ${workingDir}/bin/
        """
    }
}

