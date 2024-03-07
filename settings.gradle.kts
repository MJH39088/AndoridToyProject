pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
        jcenter()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
        maven(uri("https://jitpack.io"))
        jcenter()
    }
}

rootProject.name = "AndoridToyProject"
include(":app")
 