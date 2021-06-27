plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.0.10"

}

group = "me.dzikimlecz"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:20.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

javafx {
    version = "16"
    modules("javafx.controls")
}


tasks.withType<Test>() {
    useJUnitPlatform()
}

application {
    mainClass.set("me.dzikimlecz.snake.Launcher")
}