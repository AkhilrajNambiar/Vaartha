package com.example.vaartha.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vaartha.R
import com.example.vaartha.models.Article
import java.time.LocalDateTime

class NewsAdapter: RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {
    inner class ArticleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(article: Article) {
            val articleImage = itemView.findViewById<ImageView>(R.id.ivArticleImage)
            val source = itemView.findViewById<TextView>(R.id.tvSource)
            val title = itemView.findViewById<TextView>(R.id.tvTitle)
            val description = itemView.findViewById<TextView>(R.id.tvDescription)
            val publishedDate = itemView.findViewById<TextView>(R.id.tvPublishedAt)

            Glide.with(itemView).load(article.urlToImage).into(articleImage)
            source.text = article.source.name
            title.text = article.title
            description.text = article.description
            publishedDate.text = article.publishedAt
        }
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.item_article_preview, parent, false)
        return ArticleViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList.get(position)
        holder.bind(article)
        holder.itemView.setOnClickListener {
            onItemClickListener?.let { it(article) }
        }
    }

    fun setItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}






