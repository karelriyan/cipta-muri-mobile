package com.main.cipta_muri_mobile.ui.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.data.news.NewsItem
import com.main.cipta_muri_mobile.databinding.ItemNewsBinding
import com.main.cipta_muri_mobile.util.Formatters

class NewsAdapter(
    private val onItemClick: ((NewsItem) -> Unit)? = null
) : ListAdapter<NewsItem, NewsAdapter.NewsViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNewsBinding.inflate(inflater, parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NewsViewHolder(private val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NewsItem) {
            val title = item.title?.takeIf { it.isNotBlank() } ?: "Berita Tanpa Judul"
            binding.tvNewsTitle.text = title

            val subtitle = Formatters.formatTanggalIndo(item.publishedAt).ifBlank {
                item.publishedAt ?: ""
            }
            binding.tvNewsSubtitle.text = subtitle.ifBlank { "Tanggal tidak tersedia" }

            val excerpt = item.excerpt?.trim().orEmpty()
            binding.tvNewsExcerpt.isVisible = excerpt.isNotEmpty()
            binding.tvNewsExcerpt.text = excerpt

            val placeholder = R.drawable.ic_plastik
            val imageUrl = resolveImageUrl(item.thumbnailUrl)
            if (imageUrl.isNullOrBlank()) {
                binding.ivNewsImage.setImageResource(placeholder)
            } else {
                Glide.with(binding.ivNewsImage)
                    .load(imageUrl)
                    .placeholder(placeholder)
                    .error(placeholder)
                    .centerCrop()
                    .into(binding.ivNewsImage)
            }

            binding.root.isClickable = onItemClick != null
            binding.root.isFocusable = onItemClick != null
            binding.root.setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<NewsItem>() {
        override fun areItemsTheSame(oldItem: NewsItem, newItem: NewsItem): Boolean {
            return oldItem.slug == newItem.slug && oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: NewsItem, newItem: NewsItem): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val BASE_URL = "https://ciptamuri.com"
    }

    private fun resolveImageUrl(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val trimmed = raw.trim()
        return when {
            trimmed.startsWith("http", ignoreCase = true) -> trimmed
            trimmed.startsWith("/") -> BASE_URL + trimmed
            else -> "$BASE_URL/$trimmed"
        }
    }
}
