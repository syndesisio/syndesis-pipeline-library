#!/usr/bin/groovy

def call(Map parameters = [:], body) {
    def format = parameters.get('format', 'yyyyMMddHHmm')
    return new java.text.SimpleDateFormat(format).format(new Date())
}