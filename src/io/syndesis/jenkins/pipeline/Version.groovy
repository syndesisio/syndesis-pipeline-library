package io.syndesis.jenkins.pipeline

@Grab('com.github.zafarkhaja:java-semver:0.9.0')
class Version implements Comparable<Version>, Serializable {

  @Delegate
  private transient com.github.zafarkhaja.semver.Version semver

  Version() {}

  Version(String versionString) {
    this.semver = com.github.zafarkhaja.semver.Version.valueOf(versionString)
  }

  @Override
  String toString() {
    return this.semver.toString()
  }

  void incrementMajorVersion() {
    this.semver = this.semver.incrementMajorVersion()
  }

  void incrementMajorVersion(String preRelease) {
    this.semver = this.semver.incrementMajorVersion(preRelease)
  }

  void incrementMinorVersion() {
    this.semver = this.semver.incrementMinorVersion()
  }

  void incrementMinorVersion(String preRelease) {
    this.semver = this.semver.incrementMinorVersion(preRelease)
  }

  void incrementPatchVersion() {
    this.semver = this.semver.incrementPatchVersion()
  }

  void incrementPatchVersion(String preRelease) {
    this.semver = this.semver.incrementPatchVersion(preRelease)
  }

  void incrementPreReleaseVersion() {
    this.semver = this.semver.incrementPreReleaseVersion()
  }

  void incrementBuildMetadata() {
    this.semver = this.semver.incrementBuildMetadata()
  }

  void setPreReleaseVersion(String preRelease) {
    this.semver = this.semver.setPreReleaseVersion(preRelease)
  }

  void setBuildMetadata(String build) {
    this.semver = this.semver.setBuildMetadata(build)
  }

  @Override
  int compareTo(Version version) {
    return this.semver.compareTo(version.semver)
  }

  int compareWithBuildsTo(Version other) {
    return this.semver.compareWithBuildsTo(other.semver)
  }

  private void writeObject(ObjectOutputStream stream) throws IOException {
    if (this.semver != null) {
      stream.writeObject(semver.toString())
    }
  }

  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    String versionString = stream.readObject()
    this.semver = com.github.zafarkhaja.semver.Version.valueOf(versionString)
  }

  static void main(String[] args) {
    def testVersion = new Version("1.2.3-rc+build")
    println(testVersion.toString())
    testVersion.incrementMajorVersion()
    println(testVersion.toString())
    println(testVersion.compareTo(new Version("1.2.3-rc+build")))
    println(testVersion.compareTo(new Version("2.0.0")))
    println(testVersion.compareWithBuildsTo(new Version("2.0.0")))

    // Let's test serialization
    testVersion = new Version("1.2.3-rc+build")
    def bos = new ByteArrayOutputStream()
    def oos = new ObjectOutputStream(bos)
    testVersion.writeObject(oos)
    oos.close()
    def bytes = bos.toByteArray()
    bos.close()
    testVersion = new Version()
    println(testVersion)
    def bis = new ByteArrayInputStream(bytes)
    def ois = new ObjectInputStream(bis)
    testVersion.readObject(ois)
    ois.close()
    bis.close()
    println(testVersion)
  }
}
