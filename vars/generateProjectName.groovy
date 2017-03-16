#!/usr/bin/groovy

/**
 * Generates a new project name that consists of a build id and date.
 * @param prefix    The prefix to use, defaults in empty string.
 * @return
 */
def call(String prefix = '') {
    def id = buildId(prefix)
    def date = now()
    return "${id}-${date}".replaceAll('_', '-').toLowerCase()
}