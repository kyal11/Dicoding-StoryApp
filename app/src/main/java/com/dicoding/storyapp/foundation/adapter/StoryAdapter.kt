package com.dicoding.storyapp.foundation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyapp.data.datasource.remote.response.ListStoryItem
import com.dicoding.storyapp.databinding.ItemStoryBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class StoryAdapter(private val storyClickListener: StoryClickListener) :
    PagingDataAdapter<ListStoryItem, StoryAdapter.ViewHolder>(StoryDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, storyClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        fun bind(story: ListStoryItem?, storyClickListener: StoryClickListener) {
            if (story != null) {
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .into(binding.ivStory)
                binding.tvName.text = story.name
                binding.tvDesc.text = story.description
                val formattedDate = formatDate(story.createdAt)
                binding.tvDate.text = formattedDate
                itemView.setOnClickListener {
                    storyClickListener.onStoryClick(story)
                }
            }
        }
        private fun formatDate(dateString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val date = inputFormat.parse(dateString)
                date?.let { dateFormat.format(it) } ?: dateString
            } catch (e: Exception) {
                dateString
            }
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemStoryBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class StoryDiffCallback : DiffUtil.ItemCallback<ListStoryItem>() {
    override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
        return oldItem == newItem
    }
}

interface StoryClickListener {
    fun onStoryClick(story: ListStoryItem)
}