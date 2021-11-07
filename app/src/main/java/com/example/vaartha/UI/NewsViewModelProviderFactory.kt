package com.example.vaartha.UI

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vaartha.Repository.NewsRepository

// This class is needed when we have to pass in custom parameters
// to the viewModel
class NewsViewModelProviderFactory(
    val app: Application,
    val newsRepository: NewsRepository
    ): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsViewModel(app, newsRepository) as T
    }

}