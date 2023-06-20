rootProject.tasks {
    getByName("processResources") {
        mustRunAfter("client:package")
    }
}

tasks {
    create<Copy>("package") {
        dependsOn(":deleteStaticAssets") // first clean out an old build from the jar resources

        inputs.dir(
            "$projectDir/build"
        )

        outputs.dir(
            "${rootProject.projectDir}/src/main/resources/static"
        )

        from("build")
        into("${rootProject.projectDir}/src/main/resources/static")
        include("**/*")
    }
    create<Delete>("clean") {
        delete("node_modules", "build")
    }
}