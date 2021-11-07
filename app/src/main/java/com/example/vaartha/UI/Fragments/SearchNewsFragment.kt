package com.example.vaartha.UI.Fragments

import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vaartha.MainActivity
import com.example.vaartha.R
import com.example.vaartha.UI.NewsViewModel
import com.example.vaartha.Util.Resource
import com.example.vaartha.adapters.NewsAdapter
import com.example.vaartha.databinding.FragmentSavedNewsBinding
import com.example.vaartha.databinding.FragmentSearchNewsBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        newsAdapter = NewsAdapter()
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvSearchNews)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = newsAdapter
        }

        var job: Job? = null
        val etSearch = view.findViewById<EditText>(R.id.etSearch)
        etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(500L)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.searchNews(editable.toString())
                        Log.d("searchedFor", editable.toString())
                    }
                }
            }
        }

        val progressBar = view.findViewById<ProgressBar>(R.id.paginationProgressBar)
        viewModel.searchNews.observe(viewLifecycleOwner, { resource ->
            when(resource) {
                is Resource.Success -> {
                    hideProgressBar(progressBar)
                    resource.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                        Log.d("BreakingNewsFragment", newsAdapter.differ.currentList.toString())
                    }
                }
                is Resource.Loading -> {
                    showProgressBar(progressBar)
                }
                is Resource.Error -> {
                    hideProgressBar(progressBar)
                    resource.message?.let { message ->
                        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        })

        newsAdapter.setItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_searchNewsFragment_to_articleFragment, bundle)
        }
    }

    private fun hideProgressBar(bar: ProgressBar) {
        bar.visibility = View.INVISIBLE
    }

    private fun showProgressBar(bar: ProgressBar){
        bar.visibility = View.VISIBLE
    }
}

