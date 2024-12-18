
plugins {
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.apollo)
  alias(libs.plugins.android.library)
  id("maven-publish")
}

group = "com.fragments"
version = "1.0.0"

dependencies {
  add("api", project(":schema"))
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

apollo {
  service("service1") {
    packageName.set("com.service1")
    dependsOn(project(":schema"))
    outgoingVariantsConnection {
      afterEvaluate {
        addToSoftwareComponent("release")
      }
    }
  }
  service("service2") {
    packageName.set("com.service2")
    dependsOn(project(":schema"))
    outgoingVariantsConnection {
      afterEvaluate {
        addToSoftwareComponent("release")
      }
    }
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
