package io.syndesis.jenkins.pipeline

import com.google.common.collect.Lists

import java.util.stream.Stream

class Versions {

    private Versions() {}

    static Version latestVersionFromStrings(String versions) {
        return latestVersionFromStrings(versions.split("\\r?\\n").toList())
    }

    static Version latestVersionFromStrings(Collection<String> versions) {
        return latestVersionFromVersions(versions.stream().map{s -> new Version(s)})
    }

    static Version latestVersionFromVersions(Collection<Version> versions) {
        return latestVersionFromVersions(versions.stream())
    }

    static Version latestVersionFromVersions(Stream<Version> versions) {
        return versions.max(new Comparator<Version>() {
            @Override
            int compare(Version v1, Version v2) {
                return v1 <=> v2
            }
        }).orElse(null)
    }

    static void main(String[] args) {
        println(latestVersionFromStrings("1.2.3-rc+build\r\n2.2.3-rc+build\n1.2.4-rc+build\r\n"))
        println(latestVersionFromStrings(Lists.newArrayList("1.2.3-rc+build", "2.2.3-rc+build", "1.2.4-rc+build")))
        println(latestVersionFromVersions(Lists.newArrayList(new Version("2.2.3-rc+build"), new Version("1.2.3-rc+build"), new Version("1.2.4-rc+build"))))
        println(latestVersionFromVersions(Stream.of(new Version("1.2.3-rc+build"), new Version("1.2.4-rc+build"), new Version("2.2.3-rc+build"))))
        println(latestVersionFromVersions(Stream.of(new Version("2.2.3-rc+build"), new Version("2.2.3-rc1+build"))))
        println(latestVersionFromVersions(Stream.of(new Version("2.2.3-rc+build2"), new Version("2.2.3-rc+build"))))
    }

}
