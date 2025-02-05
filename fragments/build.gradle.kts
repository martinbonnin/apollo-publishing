
plugins {
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.apollo)
  alias(libs.plugins.android.library)
  id("maven-publish")
}

group = "com.fragments"
version = "1.0.0"

android {
  namespace = "com.schema"
  compileSdk = libs.versions.android.sdkversion.compile.get().toInt()

  publishing {
    singleVariant("release")
  }

  flavorDimensions += "version"
  productFlavors {
    create("prod") {
      // Assigns this product flavor to the "version" flavor dimension.
      // If you are using only one dimension, this property is optional,
      // and the plugin automatically assigns all the module's flavors to
      // that dimension.
      dimension = "version"
    }
    create("staging") {
      dimension = "version"
    }
  }

  kotlinOptions {
    jvmTarget = "1.8"
  }
}

abstract class Wrapper @Inject constructor(val softwareComponentFactory: SoftwareComponentFactory)

val softwareComponent = objects.newInstance(Wrapper::class.java).softwareComponentFactory.adhoc("apollo")

configurations.configureEach {
  println("Configuration: $name")
}
apollo {
  service("prod") {
    packageName.set("com.example")
    dependsOn(project(":schema"))
    outgoingVariantsConnection {
      addToSoftwareComponent(softwareComponent)
    }
  }
  service("staging") {
    packageName.set("com.example")
    dependsOn(project(":schema"))
    outgoingVariantsConnection {
      addToSoftwareComponent(softwareComponent)
    }
  }
}

dependencies {
  listOf("Prod", "Staging").forEach { variant ->
    listOf("Ir", "OtherOptions", "CodegenMetadata", "CodegenSchema").forEach { configuration ->
      add("apollo${variant}${configuration}", project(":schema")){
        capabilities {
          requireCapability("com.schema:schema-${variant.lowercase()}")
        }
      }
    }
  }
  add("prodApi", project(":schema")) {
    capabilities {
      requireCapability("com.schema:schema-prod")
    }
  }

  add("stagingApi", project(":schema")) {
    capabilities {
      requireCapability("com.schema:schema-staging")
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
