package org.owntracks.android.ui.regions

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import org.owntracks.android.ui.base.view.MvvmView
import org.owntracks.android.ui.base.viewmodel.BaseViewModel

@InstallIn(ActivityComponent::class)
@Module
abstract class RegionsActivityModule {

    @Binds
    abstract fun bindViewModel(viewModel: RegionsViewModel): BaseViewModel<MvvmView>
}