allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        jcenter()
        google()
    }
}

tasks.register("clean", Delete::class) {
    delete("build")
}