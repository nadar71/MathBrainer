package eu.indiewalkabout.fridgemanager.core.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import eu.indiewalkabout.mathbrainer.feat_statistics.data.local.db.MathBrainerDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFoodDatabase(
        @ApplicationContext context: Context
    ): MathBrainerDatabase {
        return MathBrainerDatabase.getDbInstance(context)
    }

    @Provides
    @Singleton
    fun provideMathBrainerDao(mathBrainerDatabase: MathBrainerDatabase) = mathBrainerDatabase.mathBrainerDbDao()

}

