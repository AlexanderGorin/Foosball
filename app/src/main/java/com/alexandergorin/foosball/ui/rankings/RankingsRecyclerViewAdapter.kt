package com.alexandergorin.foosball.ui.rankings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alexandergorin.foosball.databinding.RankingsItemBinding

class RankingsRecyclerViewAdapter(
    private val items: List<RankingState>
) : RecyclerView.Adapter<RankingsRecyclerViewAdapter.ItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding =
            RankingsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.setData(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ItemHolder(
        private val binding: RankingsItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun setData(model: RankingState) {
            binding.ranking.text = model.id
            binding.person.text = model.personName
            binding.gamesPlayedTextView.text = model.gamesPlayed
            binding.gamesWonTextView.text = model.gamesWon
        }
    }
}