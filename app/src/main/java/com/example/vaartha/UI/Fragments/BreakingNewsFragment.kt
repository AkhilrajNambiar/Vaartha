package com.example.vaartha.UI.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vaartha.MainActivity
import com.example.vaartha.R
import com.example.vaartha.UI.NewsViewModel
import com.example.vaartha.Util.Constants.Companion.TAG
import com.example.vaartha.Util.Resource
import com.example.vaartha.adapters.NewsAdapter
import com.example.vaartha.databinding.FragmentBreakingNewsBinding

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        newsAdapter = NewsAdapter()
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvBreakingNews)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = newsAdapter
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
        val progressBar = view.findViewById<ProgressBar>(R.id.paginationProgressBar)

        viewModel.breakingNews.observe(viewLifecycleOwner, { resource ->
            when(resource) {
                is Resource.Success -> {
                    hideProgressBar(progressBar)
                    resource.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        //The reason we add two is because
                        // first, we have an integer division and to avoid the roundoff page loss
                        // we have to add 1.
                        // secondly, we have the last page of the response which is always empty
                        val totalPages = newsResponse.totalResults / 20 + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
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
            findNavController().navigate(R.id.action_breakingNewsFragment_to_articleFragment, bundle)
        }
    }

    private fun hideProgressBar(bar: ProgressBar) {
        bar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar(bar: ProgressBar){
        bar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object: RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val scrolledOutItems = layoutManager.findFirstVisibleItemPosition()
            val visibleItems = layoutManager.childCount
            val totalItems = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            if (totalItems == scrolledOutItems + visibleItems && isScrolling && isNotLoadingAndNotLastPage) {
                viewModel.getBreakingNews("in")
                isScrolling = false
            }
            else {
                recyclerView.setPadding(0, 0, 0, 0)
            }
        }
    }
}