package hr.ferit.lifequalityapp.di

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import hr.ferit.lifequalityapp.sensing.sensors.Barometer
import hr.ferit.lifequalityapp.sensing.sensors.HumiditySensor
import hr.ferit.lifequalityapp.sensing.sensors.Thermometer
import hr.ferit.lifequalityapp.ui.viewmodels.BarometerViewModel
import hr.ferit.lifequalityapp.ui.viewmodels.HumiditySensorViewModel
import hr.ferit.lifequalityapp.ui.viewmodels.PermissionViewModel
import hr.ferit.lifequalityapp.ui.viewmodels.RadioButtonViewModel
import hr.ferit.lifequalityapp.ui.viewmodels.ServiceToggleViewModel
import hr.ferit.lifequalityapp.ui.viewmodels.SignInViewModel
import hr.ferit.lifequalityapp.ui.viewmodels.ThermometerViewModel
import hr.ferit.lifequalityapp.ui.viewmodels.TokensViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val sensorsModule = module {
    single<Barometer> { Barometer(androidContext()) }
    single<HumiditySensor> { HumiditySensor(androidContext()) }
    single<Thermometer> { Thermometer(androidContext()) }
}

val viewModelModule = module {
    viewModel<BarometerViewModel> { BarometerViewModel(get<Barometer>()) }
    viewModel<HumiditySensorViewModel> { HumiditySensorViewModel(get<HumiditySensor>()) }
    viewModel<ThermometerViewModel> { ThermometerViewModel(get<Thermometer>()) }
    viewModel<RadioButtonViewModel> { RadioButtonViewModel() }
    viewModel<ServiceToggleViewModel> { ServiceToggleViewModel() }
    viewModel<TokensViewModel> { TokensViewModel() }
    viewModel<SignInViewModel> { SignInViewModel() }
    viewModel<PermissionViewModel> { PermissionViewModel() }
}

val locationClientModule = module {
    factory<FusedLocationProviderClient> { LocationServices.getFusedLocationProviderClient(androidContext()) }
}
