package com.example.notefirebase.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.notefirebase.R
import com.example.notefirebase.firebasemodel.Note
import com.example.notefirebase.fragments.create.note.and.projects.DeleteNoteFragment
import com.example.notefirebase.utils.Helper

class NoteAdapter(private val activity: FragmentActivity, private val noteType: Int) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private var notes: List<Note> = emptyList()
    private var onClickListener: OnClickListener? = null
    private lateinit var helper: Helper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        helper = Helper(activity)
        return NoteViewHolder(itemView)
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.noteName)

        init {
            // Set long click listener
            itemView.setOnLongClickListener {
                helper.vibrateDevice()
                val selectedNote = notes[adapterPosition]
                val deleteNoteInDir = DeleteNoteFragment(0, selectedNote)
                val deleteNoteInProject = DeleteNoteFragment(1, selectedNote)
                if (noteType == 0) {
                    // Delete selected note in directory
                    deleteNoteInDir.show(
                        activity.supportFragmentManager,
                        "DeleteSelectedNoteInDirectory"
                    )
                } else {
                    // Delete selected note in project
                    deleteNoteInProject.show(
                        activity.supportFragmentManager,
                        "DeleteSelectedNoteInProject"
                    )
                }
                true
            }
        }
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = notes[position]
        holder.nameTextView.text = currentNote.noteName

        holder.itemView.setOnClickListener {
            onClickListener?.onClick(currentNote)
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    fun setNotes(noteList: List<Note>) {

        notes = noteList.reversed()
        notifyDataSetChanged()
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(note: Note)
    }
}
