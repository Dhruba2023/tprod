package com.digiwh.tprod.di

import android.content.Context
import androidx.room.Room
import com.digiwh.MainApplication
import com.digiwh.tprod.room.Databases
import com.digiwh.tprod.utils.DataExporter
import com.digiwh.tprod.utils.DataExporterImpl
import com.digiwh.tprod.utils.DateChecker
import com.digiwh.tprod.utils.DateCheckerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule{

    @Provides
    fun providesRoomDb(@ApplicationContext context: Context): Databases {
        return Room.databaseBuilder(context.applicationContext,
            Databases::class.java,
            "databases").build()
    }

    @Provides
    fun providesApplicationInstance(@ApplicationContext context: Context): MainApplication {
        return context as MainApplication
    }

    @Provides
    fun provideDataExporter(@ApplicationContext context: Context): DataExporter {
        return DataExporterImpl(context)
    }

    @Provides
    fun provideDateTimeChecker(): DateChecker {
        return DateCheckerImpl()
    }

}