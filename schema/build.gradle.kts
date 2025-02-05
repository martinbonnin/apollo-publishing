
plugins {
  id("org.jetbrains.kotlin.jvm")
  alias(libs.plugins.apollo)
  id("maven-publish")
}

group = "com.schema"
version = "1.0.0"



java {
  sourceSets.create("prod")
  sourceSets.create("staging")
  registerFeature("prod") {
    usingSourceSet(sourceSets["prod"])
  }
  registerFeature("staging") {
    usingSourceSet(sourceSets["staging"])
  }
}

dependencies {
  add("prodApi", libs.apollo.api)
  add("stagingApi", libs.apollo.api)
}

apollo {
  service("prod") {
    packageName.set("com.example")
    generateApolloMetadata.set(true)
    alwaysGenerateTypesMatching.add(".*")
    outputDirConnection {
      connectToJavaSourceSet("prod")
    }
    outgoingVariantsConnection {
      afterEvaluate {
        addToSoftwareComponent("java")
      }
    }
  }
  service("staging") {
    packageName.set("com.example")
    generateApolloMetadata.set(true)
    alwaysGenerateTypesMatching.add(".*")
    outputDirConnection {
      connectToJavaSourceSet("staging")
    }
    outgoingVariantsConnection {
      afterEvaluate {
        addToSoftwareComponent("java")
      }
    }
  }
}

configure<PublishingExtension> {
  publications {
    create<MavenPublication>("default") {
      afterEvaluate {
        from(components["java"])
      }
    }
  }
  repositories {
    maven {
      name = "localMaven"
      url = uri("file://${rootDir}/build/localMaven")
    }
  }
}
