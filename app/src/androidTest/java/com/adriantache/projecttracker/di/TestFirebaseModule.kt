package com.adriantache.projecttracker.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [FirebaseModule::class]
)
object TestFirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        val database = FirebaseDatabase.getInstance()
        try {
            database.useEmulator("10.0.2.2", 9000)
        } catch (e: Exception) {
            // Emulator might already be configured or not running
        }
        return database
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        val auth = FirebaseAuth.getInstance()
        try {
            auth.useEmulator("10.0.2.2", 9099)
        } catch (e: Exception) {
            // Emulator might already be configured or not running
        }
        return auth
    }
}
