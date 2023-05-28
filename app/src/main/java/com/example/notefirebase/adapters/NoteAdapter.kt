package com.example.notefirebase.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notefirebase.R
import com.example.notefirebase.firebasemodel.Note

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private var notes: List<Note> = emptyList()
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(itemView)
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.noteName)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = notes[position]
        holder.nameTextView.text = currentNote.name

        holder.itemView.setOnClickListener {
            onClickListener?.onClick(currentNote)
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    fun setNotes(noteList: List<Note>) {
        notes = noteList
        notifyDataSetChanged()
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(note: Note)
    }
}
