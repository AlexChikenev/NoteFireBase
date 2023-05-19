//package com.example.notefirebase.adapters
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.notefirebase.model.Wish
//import com.example.notefirebase.R
//
//class WishAdapter(var wish: List<Wish>) : RecyclerView.Adapter<WishAdapter.WishViewHolder>() {
//    class WishViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val wishName: TextView = itemView.findViewById(R.id.wishName)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishViewHolder {
//        val itemView =
//            LayoutInflater.from(parent.context).inflate(R.layout.wish_item, parent, false)
//        return WishViewHolder(itemView)
//    }
//
//    override fun onBindViewHolder(holder: WishViewHolder, position: Int) {
//        val currentWish = wish[position]
//        holder.wishName.text = currentWish.name
//    }
//
//    override fun getItemCount() = wish.size
//}