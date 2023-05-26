package com.example.notefirebase.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notefirebase.R
import com.example.notefirebase.firebasemodel.Directory

class DirectoryAdapter : RecyclerView.Adapter<DirectoryAdapter.DirectoryViewHolder>() {

    private var directories: List<Directory> = emptyList()
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoryViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_directory, parent, false)
        return DirectoryViewHolder(itemView)
    }

    inner class DirectoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameOfDirectory)
    }

    override fun onBindViewHolder(holder: DirectoryViewHolder, position: Int) {
        val currentDirectory = directories[position]
        holder.nameTextView.text = currentDirectory.name

        holder.itemView.setOnClickListener {
            onClickListener?.onClick(currentDirectory)
        }
    }

    override fun getItemCount(): Int {
        return directories.size
    }

    fun setDirectories(directoryList: List<Directory>) {
        directories = directoryList
        notifyDataSetChanged()
    }


    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(directory: Directory)
    }
}
