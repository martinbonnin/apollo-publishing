
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

abstract class Wrapper @Inject constructor(val softwareComponentFactory: SoftwareComponentFactory)

val softwareComponent = objects.newInstance(Wrapper::class.java).softwareComponentFactory.adhoc("apollo")

apollo {
  service("service1") {
    packageName.set("com.service1")
    dependsOn(project(":schema"))
    outgoingVariantsConnection {
      addToSoftwareComponent(softwareComponent)
    }
  }
  service("service2") {
    packageName.set("com.service2")
    dependsOn(project(":schema"))
    outgoingVariantsConnection {
      addToSoftwareComponent(softwareComponent)
    }
  }
}

configure<PublishingExtension> {
  publications {
    create<MavenPublication>("default") {
      artifact("${layout.buildDirectory.get().asFile}/intermediates/aar_main_jar/release/classes.jar")
    }
    create<MavenPublication>("apollo") {
      artifactId = "fragments-apollo"
      from(softwareComponent)
    }
  }
  repositories {
    maven {
      name = "localMaven"
      url = uri("file://${rootDir}/build/localMaven")
    }
  }
}
