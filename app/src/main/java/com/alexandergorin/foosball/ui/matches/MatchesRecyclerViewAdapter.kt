package com.alexandergorin.foosball.ui.matches

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alexandergorin.foosball.databinding.MatchesItemBinding

class MatchesRecyclerViewAdapter(
    private val items: List<MatchState>,
    private val onMatchClick: (Int) -> Unit
) : RecyclerView.Adapter<MatchesRecyclerViewAdapter.ItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding =
            MatchesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(itemBinding, onMatchClick)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.setData(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ItemHolder(
        private val binding: MatchesItemBinding,
        private val onMatchClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun setData(model: MatchState) {
            binding.personOne.text = model.firstPersonName
            binding.personTwo.text = model.secondPersonName
            binding.scoreOne.text = model.firstPersonScore
            binding.scoreTwo.text = model.secondPersonScore
            binding.cardView.setOnClickListener {
                onMatchClick(model.id)
            }
        }
    }
}