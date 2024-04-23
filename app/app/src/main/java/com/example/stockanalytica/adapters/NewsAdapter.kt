package com.example.stockanalytica.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stockanalytica.R
import com.example.stockanalytica.model.NewsArticle
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class NewsAdapter(private val newsList: List<NewsArticle>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position]
        holder.bind(news)
    }

    override fun getItemCount() = newsList.size

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val title: TextView = itemView.findViewById(R.id.news_title)
        private val summary: TextView = itemView.findViewById(R.id.news_desc)
        private val bannerImage: ImageView = itemView.findViewById(R.id.news_image)
        private val chipGroup: ChipGroup = itemView.findViewById(R.id.chip_group)
        private val readMoreButton: Button = itemView.findViewById(R.id.read_more_button)

        fun bind(news: NewsArticle) {
            title.text = news.title
            summary.text = news.summary
            if (news.banner_image != null) {
                bannerImage.visibility = View.VISIBLE
                Glide.with(itemView.context).load(news.banner_image).into(bannerImage)
            } else {
                bannerImage.visibility = View.GONE
            }
            readMoreButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(news.url))
                itemView.context.startActivity(intent)
            }
            chipGroup.removeAllViews()
            news.topics.forEach { topic ->
                val chip = Chip(itemView.context)
                chip.text = topic.topic
                chipGroup.addView(chip)
            }
        }
    }
}
