package com.dimensicodes.virtualbatiktryon.ui.pattern

import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dimensicodes.virtualbatiktryon.data.source.local.remote.response.BatikItem
import com.dimensicodes.virtualbatiktryon.databinding.ItemGridBatikBinding

class GridBatikAdapter() :
    RecyclerView.Adapter<GridBatikAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback : OnitemCLickCallback

    fun setOnItemClickCallBack(onItemCLickCallback: OnitemCLickCallback){
        this.onItemClickCallback = onItemCLickCallback
    }

    interface OnitemCLickCallback {
        fun onItemClicked(data:BatikItem)
    }

    var listBatik = ArrayList<BatikItem>()
            set(listBatik) {
                if(listBatik.size > 0 ){
                    this.listBatik.clear()
                }
                this.listBatik.addAll(listBatik)
                notifyDataSetChanged()
            }

    private var selectedPosition = -1

    inner class ViewHolder(private val binding: ItemGridBatikBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(batik: BatikItem, position: Int) {
            Glide.with(itemView)
                .load(Uri.parse(batik.imagePath))
                .apply(RequestOptions().override(350, 550))
                .into(binding.imgBatik)
            if (selectedPosition==position){
                itemView.setBackgroundColor(Color.BLUE)
            }else{
                itemView.setBackgroundColor(Color.TRANSPARENT)
            }
            itemView.setOnClickListener {
                selectedPosition = position
                onItemClickCallback.onItemClicked(batik)
                notifyDataSetChanged()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemGridBatikBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listBatik[position], position)
    }

    override fun getItemCount(): Int = listBatik.size
}