
plugins {
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.apollo)
  alias(libs.plugins.android.library)
  id("maven-publish")
}

group = "com.schema"
version = "1.0.0"

dependencies {
  add("api", libs.apollo.api)
}

apollo {
  service("service1") {
    packageName.set("com.service1")
    generateApolloMetadata.set(true)
    alwaysGenerateTypesMatching.add(".*")
    outgoingVariantsConnection {
      afterEvaluate {
        addToSoftwareComponent("release")
      }
    }
  }
  service("service2") {
    packageName.set("com.service2")
    generateApolloMetadata.set(true)
    alwaysGenerateTypesMatching.add(".*")
    outgoingVariantsConnection {
      afterEvaluate {
        addToSoftwareComponent("release")
      }
    }
  }
}

android {
  namespace = "com.schema"
  compileSdk = libs.versions.android.sdkversion.compile.get().toInt()
  publishing {
    singleVariant("release")
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
}

configure<PublishingExtension> {
  publications {
    create<MavenPublication>("default") {
      afterEvaluate {
        from(components["release"])
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
