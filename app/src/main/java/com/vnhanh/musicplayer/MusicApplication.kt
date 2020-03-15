package com.vnhanh.musicplayer

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.vnhanh.musicplayer.basedagger.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.HasAndroidInjector

class MusicApplication : DaggerApplication(), HasAndroidInjector, LifecycleObserver {
    private val androidInjector : AndroidInjector<MusicApplication> by lazy {
        DaggerAppComponent.factory().create(this)
    }

    override fun applicationInjector(): AndroidInjector<MusicApplication> = androidInjector

    override fun onCreate() {
        super.onCreate()
        androidInjector.inject(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onAppForeground(){

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onAppBackground(){

    }
}