#!/usr/bin/groovy

/**
 * Runs the body when invoked against the master branch.
 * @return
 */
def call(body) {
    if ("master".equalsIgnoreCase("${env.BRANCH_NAME}")) {
        body()
    }
}