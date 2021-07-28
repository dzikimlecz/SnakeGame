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
    implementation("org.jetbrains:annotations:21.0.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
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