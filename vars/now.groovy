#!/usr/bin/groovy

/**
 * Returns the current date in a customizable text format.
 * @param format The format to use, defaults to 'yyyyMMddHHmm'.
 * @return
 */
def call(String format = 'yyyyMMddHHmm') {
    return new java.text.SimpleDateFormat(format).format(new Date())
}