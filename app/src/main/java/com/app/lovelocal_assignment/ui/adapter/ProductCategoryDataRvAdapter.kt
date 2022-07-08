package com.app.lovelocal_assignment.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.lovelocal_assignment.R
import com.app.lovelocal_assignment.databinding.ItemProductCetegoryDataBinding
import com.app.lovelocal_assignment.model.CategoryData
import com.app.lovelocal_assignment.model.Product
import com.bumptech.glide.Glide

class ProductCategoryDataRvAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var dataList: MutableList<Product> = ArrayList()
    private var mOnItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemBinding = DataBindingUtil.inflate<ItemProductCetegoryDataBinding>(
            LayoutInflater.from(context),
            R.layout.item_product_cetegory_data, parent, false
        )
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ViewHolder
        viewHolder.setData(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    private fun add(data: Product) {
        dataList.add(data)
        notifyItemInserted(dataList.size - 1)
    }

    fun addAtPosition(position: Int, data: Product) {
        dataList.add(position, data)
        notifyItemInserted(position)
    }

    fun addAll(dataList: List<Product>) {
        dataList.forEach { data ->
            add(data)
        }
    }

    private fun remove(data: Product) {
        val position = dataList.indexOf(data)
        if (position > -1) {
            dataList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        dataList.clear()
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Product {
        return dataList[position]
    }

    fun setItemAtPosition(position: Int, data: Product) {
        dataList[position] = data
        notifyItemChanged(position)
    }

    fun removeItem(position: Int) {
        remove(dataList[position])
    }

    fun size(): Int {
        return dataList.size
    }

    internal inner class ViewHolder(var itemBinding: ItemProductCetegoryDataBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun setData(data: Product) {

            itemBinding.data = data

            Glide.with(context)
                .load(data.image)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .centerCrop()
                .into(itemBinding.ivImage)

            itemBinding.rlRoot.setOnClickListener {
                mOnItemClickListener?.onItemClick(data, adapterPosition)
            }
        }
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        mOnItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(data: Product, position: Int)
    }
}
