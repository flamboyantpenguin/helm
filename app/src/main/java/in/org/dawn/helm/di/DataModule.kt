package `in`.org.dawn.helm.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import `in`.org.dawn.helm.prefs.LanternRepository
import `in`.org.dawn.helm.prefs.SteerRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // This makes the repos live as long as the app
object DataModule {

    @Provides
    @Singleton
    fun provideLanternRepo(@ApplicationContext context: Context): LanternRepository {
        return LanternRepository(context)
    }

    @Provides
    @Singleton
    fun provideSteerRepo(@ApplicationContext context: Context): SteerRepository {
        return SteerRepository(context)
    }
}