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

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SkillSlot"

include(":app")
include(":core:model")
include(":core:data")
include(":core:domain")
include(":feature-slot")
include(":feature-puzzle")
include(":feature-progression")
include(":feature-leaderboard")
include(":puzzle-engine")
include(":puzzles:puzzle-wordsearch")
include(":puzzles:puzzle-sudoku")
include(":puzzles:puzzle-ballsort")
include(":puzzles:puzzle-maze")
include(":puzzles:puzzle-boggle")
include(":puzzles:puzzle-memory")
include(":puzzles:puzzle-nonogram")
include(":puzzles:puzzle-sliding")
include(":puzzles:puzzle-connect")
include(":puzzles:puzzle-sequence")
