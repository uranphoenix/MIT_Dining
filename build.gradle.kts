plugins {
    id("java")
}

group = "org.mitdining"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.telegram:telegrambots:6.0.1")
    implementation("org.slf4j:slf4j-nop:2.0.16")
    implementation("com.google.code.gson:gson:2.13.1")
    implementation("org.jsoup:jsoup:1.14.3")
}

tasks.test {
    useJUnitPlatform()
}