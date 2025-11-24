package eu.indiewalkabout.mathbrainer.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import eu.indiewalkabout.mathbrainer.feat_statistics.data.local.db.MathBrainerDbDao
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.repository.MathBrainerRepository
import eu.indiewalkabout.mathbrainer.feat_statistics.domain.repository.MathBrainerRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMathBrainerRepository(
        mathBrainerDao: MathBrainerDbDao
    ): MathBrainerRepository {
        return MathBrainerRepositoryImpl(mathBrainerDao)
    }
}
