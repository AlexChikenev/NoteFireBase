package com.example.notefirebase.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.notefirebase.R
import com.example.notefirebase.firebasemodel.Directory
import com.example.notefirebase.fragments.create.note.and.projects.DeleteDirectoryFragment
import com.example.notefirebase.utils.Helper
import kotlin.math.roundToInt

class DirectoryAdapter(private val activity: FragmentActivity, private val context: Context) :
    RecyclerView.Adapter<DirectoryAdapter.DirectoryViewHolder>() {

    private var directories: List<Directory> = emptyList()
    private var onClickListener: OnClickListener? = null
    private lateinit var helper: Helper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoryViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_directory, parent, false)
        helper = Helper(activity)
        return DirectoryViewHolder(itemView)
    }

    inner class DirectoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameOfDirectory)
        val cardView: CardView = itemView.findViewById(R.id.cardNote)
        init {
            // Set long click listener
            itemView.setOnLongClickListener {
                helper.vibrateDevice()
                // Delete selected task
                val selectedDirectory = directories[adapterPosition]
                val deleteDirectory = DeleteDirectoryFragment(selectedDirectory)
                deleteDirectory.show(activity.supportFragmentManager, "DeleteSelectedDirectory")
                true
            }
        }
    }

    override fun onBindViewHolder(holder: DirectoryViewHolder, position: Int) {
        val currentDirectory = directories[position]
        holder.nameTextView.text = currentDirectory.directoryName

        val layoutParams = holder.cardView.layoutParams as ViewGroup.MarginLayoutParams
        val margin = helper.dpToPx(context, 20.0f)
            layoutParams.marginEnd = margin.roundToInt()

        holder.itemView.setOnClickListener {
            onClickListener?.onClick(currentDirectory)
        }
    }

    override fun getItemCount(): Int {
        return directories.size
    }

    fun setDirectories(directoryList: List<Directory>) {
        directories = directoryList.reversed()
        notifyDataSetChanged()
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(directory: Directory)
    }
}
