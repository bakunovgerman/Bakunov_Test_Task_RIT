package com.example.bakunov_test_task.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.bakunov_test_task.R
import com.example.bakunov_test_task.data.ItemRss
import com.squareup.picasso.Picasso

class ItemsAdapter(private val list: MutableList<ItemRss>?, val onPopularNowItemClick: (ItemRss) -> Unit) : RecyclerView.Adapter<ItemsAdapter.ItemsRssViewHolder>() {

    inner class ItemsRssViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val itemRoot: CardView = itemView.findViewById(R.id.cvItemRoot)
        private val itemTitleText: TextView = itemView.findViewById(R.id.tvItemTitle)
        private val itemCategoryText: TextView = itemView.findViewById(R.id.tvItemCategory)
        private val itemDescriptionText: TextView = itemView.findViewById(R.id.tvItemDecoration)
        private val itemImage: ImageView = itemView.findViewById(R.id.imgItem)

        private lateinit var item: ItemRss

        init {
            itemRoot.setOnClickListener {
                onPopularNowItemClick.invoke(item)
            }
        }

        fun bind(itemRss: ItemRss) {
            item = itemRss
            itemTitleText.text = itemRss.title
            itemCategoryText.text = itemRss.category
            itemDescriptionText.text = itemRss.description
            if (itemImage != null)
                Picasso.get().load(itemRss.imgUrl).into(itemImage)
            else
                itemImage.

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsRssViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemsRssViewHolder(inflater.inflate(R.layout.item_rss_with_image, parent, false))
    }

    override fun onBindViewHolder(holder: ItemsRssViewHolder, position: Int) {
        holder.bind(list!![position])
        holder.itemRoot.setOnClickListener { onPopularNowItemClick.invoke(list[position]) }
    }

    override fun getItemCount(): Int = list!!.size
}