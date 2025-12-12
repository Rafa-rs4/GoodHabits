pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }

    // 👇 Esto carga tu archivo gradle/libs.versions.toml
    //versionCatalogs {
    //    create("libs") {
    //        from(files("../gradle/libs.versions.toml"))
    //    }
    //}
}

rootProject.name = "GoodHabits"
include(":app")

