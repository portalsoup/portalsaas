pluginManagement {
    repositories {
        maven {
            url = uri("https://portalsoup.nyc3.digitaloceanspaces.com/")
        }
        gradlePluginPortal()
    }
}

rootProject.name = "portalsaas"

include("deploy")