package com.vnhanh.musicplayer.basedagger

import android.content.Context
import com.vnhanh.musicplayer.MusicApplication
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class AppModule {

    @Singleton
    @Binds
    abstract fun bindContext(app:MusicApplication) : Context

}