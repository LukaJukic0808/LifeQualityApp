package hr.ferit.lifequalityapp

import android.app.Application
import hr.ferit.lifequalityapp.di.locationClientModule
import hr.ferit.lifequalityapp.di.sensorsModule
import hr.ferit.lifequalityapp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class LifeQualityApp : Application(){
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@LifeQualityApp)
            modules(viewModelModule, sensorsModule, locationClientModule)
        }
    }
}