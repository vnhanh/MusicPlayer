package com.vnhanh.musicplayer.basedagger

import com.vnhanh.musicplayer.MusicApplication
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AndroidSupportInjectionModule::class, AppModule::class]
)
interface AppComponent : AndroidInjector<MusicApplication>{
    @Component.Factory
    abstract class Factorys : AndroidInjector.Factory<MusicApplication>
}