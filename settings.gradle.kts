pluginManagement {
  listOf(repositories, dependencyResolutionManagement.repositories).forEach {
    it.mavenCentral()
    it.maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    it.google()
    it.maven("file://${rootDir}/build/localMaven")
    it.maven("https://storage.googleapis.com/apollo-previews/m2/")
  }
}


include(":schema")
include(":fragments")
include(":consumer")