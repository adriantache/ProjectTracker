package com.adriantache.projecttracker

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.base.DescribedPredicate.alwaysTrue
import com.tngtech.archunit.base.DescribedPredicate.not
import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage
import com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage
import com.tngtech.archunit.core.domain.properties.HasName.Predicates.nameMatching
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.junit.Test

class ArchitectureTest {

    private val importedClasses = ClassFileImporter()
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
        .importPackages("com.adriantache.projecttracker")

    @Test
    fun `domain layer should not depend on data or ui layers`() {
        noClasses()
            .that().resideInAPackage("..domain..")
            .and().haveSimpleNameNotEndingWith("Test")
            .and().haveSimpleNameNotContaining("Hilt")
            .and().haveSimpleNameNotContaining("Dagger")
            .and().haveSimpleNameNotEndingWith("_Factory")
            .and().haveSimpleNameNotEndingWith("_HiltModules")
            .should().dependOnClassesThat().resideInAnyPackage(
                "com.adriantache.projecttracker.data..",
                "com.adriantache.projecttracker.ui.."
            )
            .check(importedClasses)
    }

    @Test
    fun `domain layer should not depend on android framework`() {
        noClasses()
            .that().resideInAPackage("..domain..")
            .and().haveSimpleNameNotEndingWith("Test")
            .and().haveSimpleNameNotContaining("Hilt")
            .and().haveSimpleNameNotContaining("Dagger")
            .should().dependOnClassesThat().resideInAPackage("android..")
            .because("the domain layer must be pure Kotlin and platform-independent")
            .check(importedClasses)
    }

    @Test
    fun `ui should not access repositories`() {
        noClasses()
            .that().resideInAPackage("com.adriantache.projecttracker.ui..")
            .and().haveSimpleNameNotEndingWith("Test")
            .and().haveSimpleNameNotContaining("Hilt")
            .and().haveSimpleNameNotContaining("Dagger")
            .should().dependOnClassesThat().resideInAnyPackage("..data..")
            .because("the UI should only interact with the Domain via UseCases and their State")
            .check(importedClasses)
    }

    @Test
    fun `ui layer should only access specific domain components`() {
        // 1. Define allowed domain access using Regex to catch inner classes (the $ symbol)
        val allowedDomainAccess: DescribedPredicate<JavaClass> =
            // Matches domain.MyUseCase and domain.MyUseCase$Params
            resideInAPackage("..domain")
                .and(nameMatching(".*UseCase.*"))
                .or(
                    // Matches anything inside the state package (more resilient)
                    resideInAPackage("..domain.state..")
                )
                .or(
                    // Matches anything inside the entity package
                    resideInAPackage("..domain.entity..")
                )

        noClasses()
            .that().resideInAPackage("..ui..")
            // 2. Filter out technical noise
            .and().haveSimpleNameNotEndingWith("Test")
            .and().haveSimpleNameNotContaining("_") // Ignore Hilt/Dagger
            // We allow the generated Compose lambdas ($) to be checked,
            // they just need to follow the domain rules below.
            .should().dependOnClassesThat(
                resideInAPackage("..domain..")
                    .and(not(allowedDomainAccess))
            )
            .check(importedClasses)
    }

    @Test
    fun `layered architecture should be respected`() {
        layeredArchitecture()
            .consideringOnlyDependenciesInAnyPackage("com.adriantache.projecttracker..")
            .layer("UI").definedBy("com.adriantache.projecttracker.ui..")
            .layer("Data").definedBy("com.adriantache.projecttracker.data..")
            .layer("Domain").definedBy("com.adriantache.projecttracker.domain..")
            .layer("DI").definedBy("com.adriantache.projecttracker.di..")

            .whereLayer("UI").mayOnlyBeAccessedByLayers("DI")
            .whereLayer("Data").mayOnlyBeAccessedByLayers("DI")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("UI", "Data", "DI")

            // Ignore common generated code patterns and tests
            .ignoreDependency(alwaysTrue(), resideInAnyPackage("..hilt_aggregated_deps.."))
            .ignoreDependency(resideInAnyPackage("..hilt_aggregated_deps.."), alwaysTrue())

            .ignoreDependency(nameMatching(".*_HiltComponents.*"), alwaysTrue())
            .ignoreDependency(alwaysTrue(), nameMatching(".*_HiltComponents.*"))

            .ignoreDependency(nameMatching(".*DaggerProjectTrackerApplication.*"), alwaysTrue())
            .ignoreDependency(nameMatching(".*ComposableSingletons.*"), alwaysTrue())

            .ignoreDependency(nameMatching(".*_HiltModules.*"), alwaysTrue())
            .ignoreDependency(alwaysTrue(), nameMatching(".*_HiltModules.*"))

            .ignoreDependency(nameMatching(".*_Factory"), alwaysTrue())
            .ignoreDependency(nameMatching(".*_MembersInjector"), alwaysTrue())

            .check(importedClasses)
    }
}
