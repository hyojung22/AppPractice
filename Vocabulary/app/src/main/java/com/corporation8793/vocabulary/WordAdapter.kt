package com.corporation8793.vocabulary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.corporation8793.vocabulary.databinding.ItemWordBinding

class WordAdapter(
    val list: MutableList<Word>,
    private val itemClickListener: ItemClickListener? = null,
) : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = ItemWordBinding.inflate(inflater, parent, false)
        return WordViewHolder(binding)
    }
    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = list[position]
        holder.bind(word)
        holder.itemView.setOnClickListener { itemClickListener?.onClick(word) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class WordViewHolder(val binding: ItemWordBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(word: Word) {
            binding.apply {
                tvWord.text = word.text
                tvMean.text = word.mean
                chipType.text = word.type
            }
        }
    }

    interface ItemClickListener {
        fun onClick(word: Word)
    }
}