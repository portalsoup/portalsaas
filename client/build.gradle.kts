tasks.create<Copy>("package") {
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

tasks.create<Delete>("clean") {
    delete("node_modules", "build")
}