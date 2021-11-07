package com.example.vaartha.UI

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.*
import com.example.vaartha.NewsApplication
import com.example.vaartha.Repository.NewsRepository
import com.example.vaartha.Util.Resource
import com.example.vaartha.models.Article
import com.example.vaartha.models.NewsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.lang.Exception

class NewsViewModel(
    app: Application,
    val newsRepository: NewsRepository
): AndroidViewModel(app){

    private var _breakingNews = MutableLiveData<Resource<NewsResponse>>()
    val breakingNews: LiveData<Resource<NewsResponse>> = _breakingNews
    var breakingNewsResponse: NewsResponse? = null
    var breakingNewsPage = 1

    private var _searchNews = MutableLiveData<Resource<NewsResponse>>()
    val searchNews: LiveData<Resource<NewsResponse>> = _searchNews
    var searchNewsResponse: NewsResponse? = null
    var searchNewsPage = 1

    init {
        try {
            getBreakingNews("in")
        }
        catch (e: Exception) {
            Log.d("BreakingNewsFragment", e.stackTraceToString())
        }
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch(Dispatchers.IO) {
        //First we emit the loading state to the fragment
        _breakingNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                // Based on the response we add the value to the liveData
                _breakingNews.postValue(handleBreakingNewsResponse(response))
            }
            else {
                _breakingNews.postValue(Resource.Error("No internet connection"))
            }
        }
        catch (t: Throwable) {
            when(t) {
                is IOException -> _breakingNews.postValue(Resource.Error("Another network failure"))
                else -> _breakingNews.postValue(Resource.Error("Conversion error"))
            }
        }
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        _searchNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                _searchNews.postValue(handleSearchNewsResponse(response))
            }
            else {
                _searchNews.postValue(Resource.Error("No internet connection"))
            }
        }
        catch (t: Throwable) {
            when(t) {
                is IOException -> _searchNews.postValue(Resource.Error("Another network failure"))
                else -> _searchNews.postValue(Resource.Error("Conversion error"))
            }
        }
    }

    // We check whether we get a successful response or error
    private fun handleBreakingNewsResponse(
        response: Response<NewsResponse>
    ): Resource<NewsResponse>{
        if (response.isSuccessful){
            response.body()?.let { resource ->
                breakingNewsPage++
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resource
                }
                else {
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resource.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resource)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(
        response: Response<NewsResponse>
    ): Resource<NewsResponse>{
        if (response.isSuccessful){
            response.body()?.let { resource ->
                searchNewsPage++
                if (searchNewsResponse == null) {
                    searchNewsResponse = resource
                }
                else {
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resource.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resource)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
        else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    else -> true
                }
            }
        }
        return false
    }

}



