import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
}
kotlin {
    kotlin.applyDefaultHierarchyTemplate()
    jvm()
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_19)
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    val xcFramework = XCFramework("logan")
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "Logan"
            isStatic = true
            xcFramework.add(this)
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.kotlinx.datetime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        val jvmMain by getting {
            dependsOn(commonMain)
            dependencies {

            }
        }
        val jvmTest by getting {
            dependencies {

            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {

            }
        }
        val androidUnitTest by getting {
            dependencies {

            }
        }
        val androidInstrumentedTest by getting {
            dependencies {

            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by getting {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {

            }
        }
        val iosTest by getting {
            dependencies {

            }
        }
    }
}
android {
    namespace = "com.binishmatheww.logan"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_19
        targetCompatibility = JavaVersion.VERSION_19
    }
    buildFeatures {
        buildConfig = true
    }
}

group = "com.binishmatheww"
val artifactId = "logan"
version = "1.0.0-SNAPSHOT"

mavenPublishing {
    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.None(),
            sourcesJar = true,
            androidVariantsToPublish = listOf("release"),
        )
    )
    coordinates(
        group.toString(),
        artifactId,
        version.toString()
    )
    pom {
        name = "Logan"
        description = "A Kotlin Multiplatform library for platform agnostic logging"
        inceptionYear = "2025"
        url = "https://bini.sh/projects/logan"
        developers {
            developer {
                id = "666"
                name = "Binish Mathew"
                email = "binishmatheww@gmail.com"
                url = "https://bini.sh"
                roles.add("Software Engineer")
            }
        }
        scm {
            url = "https://github.com/binishmatheww/logan/"
            connection = "scm:git:git://github.com/binishmatheww/logan.git"
            developerConnection = "scm:git:ssh://git@github.com/binishmatheww/logan.git"
        }
    }
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}

publishing {
    val localProperties = Properties()
    val localPropertiesFile = file("../local.properties")
    if (localPropertiesFile.canRead()) {
        localProperties.load(localPropertiesFile.inputStream())
    }
    val mavenUsername = localProperties.getProperty("mavenUsername").orEmpty()
    val mavenPassword = localProperties.getProperty("mavenPassword").orEmpty()
    if (mavenUsername.isBlank()) {
        throw Exception("mavenUsername is not set in local.properties")
    }
    if (mavenPassword.isBlank()) {
        throw Exception("mavenPassword is not set in local.properties")
    }
    val mavenUrl = uri(
        if (version.toString().endsWith("SNAPSHOT")) "https://central.sonatype.com/repository/maven-snapshots/"
        else throw Exception("URL setup is not done yet")//"https://central.sonatype.com/repository/maven-snapshots"
    )
    repositories {
        maven {
            name = "Logan"
            description = "A Kotlin Multiplatform library for platform agnostic logging"
            url = mavenUrl
            credentials {
                username = mavenUsername
                password = mavenPassword
            }
        }
    }
}