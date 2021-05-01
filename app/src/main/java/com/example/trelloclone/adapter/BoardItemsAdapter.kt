package com.example.trelloclone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trelloclone.R
import com.example.trelloclone.models.Board
import kotlinx.android.synthetic.main.item_board.view.*

open class BoardItemsAdapter(private val context: Context, private var list: ArrayList<Board>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onCLickListener: OnCLickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_board, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {
            Glide.with(context).load(model.image).centerCrop()
                .placeholder(R.drawable.ic_board_place_holder).into(holder.itemView.iv_board_image)

            holder.itemView.tv_board_name.text = model.name
            holder.itemView.tv_board_created_by.text = "Created by: ${model.createdBy}"

            holder.itemView.setOnClickListener {
                onCLickListener!!.onClick(position, model)
            }
        }
    }

    interface OnCLickListener {
        fun onClick(position: Int, model: Board)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onCLickListener: OnCLickListener) {
        this.onCLickListener=onCLickListener
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}