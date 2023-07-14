package hr.ferit.lifequalityapp.di

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import hr.ferit.lifequalityapp.sensing.models.BarometerModel
import hr.ferit.lifequalityapp.sensing.models.HumiditySensorModel
import hr.ferit.lifequalityapp.sensing.models.ThermometerModel
import hr.ferit.lifequalityapp.sensing.sensors.Barometer
import hr.ferit.lifequalityapp.sensing.sensors.HumiditySensor
import hr.ferit.lifequalityapp.sensing.sensors.Thermometer
import hr.ferit.lifequalityapp.ui.viewmodels.PermissionViewModel
import hr.ferit.lifequalityapp.ui.viewmodels.RadioButtonViewModel
import hr.ferit.lifequalityapp.ui.viewmodels.ServiceToggleViewModel
import hr.ferit.lifequalityapp.ui.viewmodels.SignInViewModel
import hr.ferit.lifequalityapp.ui.viewmodels.TokensViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val sensorsModule = module {
    single<Barometer> { Barometer(androidContext()) }
    single<HumiditySensor> { HumiditySensor(androidContext()) }
    single<Thermometer> { Thermometer(androidContext()) }
    single<BarometerModel> { BarometerModel(get<Barometer>()) }
    single<HumiditySensorModel> { HumiditySensorModel(get<HumiditySensor>()) }
    single<ThermometerModel> { ThermometerModel(get<Thermometer>()) }
}

val viewModelModule = module {
    viewModel<RadioButtonViewModel> { RadioButtonViewModel() }
    viewModel<ServiceToggleViewModel> { ServiceToggleViewModel(androidContext()) }
    viewModel<TokensViewModel> { TokensViewModel() }
    viewModel<SignInViewModel> { SignInViewModel() }
    viewModel<PermissionViewModel> { PermissionViewModel() }
}

val locationClientModule = module {
    factory<FusedLocationProviderClient> { LocationServices.getFusedLocationProviderClient(androidContext()) }
}
