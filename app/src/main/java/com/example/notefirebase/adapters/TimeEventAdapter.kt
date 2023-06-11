package com.example.notefirebase.adapters

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.notefirebase.R
import com.example.notefirebase.firebasemodel.TimeEvent
import com.example.notefirebase.fragments.create.note.and.projects.DeleteTimeEventFragment
import com.example.notefirebase.utils.FirebaseManager
import com.example.notefirebase.utils.Helper
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class TimeEventAdapter(private val activity: FragmentActivity) :
    RecyclerView.Adapter<TimeEventAdapter.TimeEventViewHolder>() {

    private var timeEvent: List<TimeEvent> = emptyList()
    private lateinit var helper: Helper
    private lateinit var firebaseManager: FirebaseManager
    val userUid = FirebaseAuth.getInstance().uid ?: ""
    private val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userUid)
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeEventViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_time_event, parent, false)
        helper = Helper(activity)
        return TimeEventViewHolder(itemView)
    }

    inner class TimeEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.eventName)
        val switcher: SwitchMaterial = itemView.findViewById(R.id.switcher)

        init {
            switcher.setOnCheckedChangeListener { _, isChecked ->
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    val currentEvent = timeEvent[currentPosition]
                    if (isChecked) {
                        currentEvent.eventIsCompleted = true
                        nameTextView.paintFlags =
                            nameTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    } else {
                        currentEvent.eventIsCompleted = false
                        nameTextView.paintFlags =
                            nameTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    }
                    updateEventInDatabase(currentEvent)
                }
            }

            // Set long click listener
            itemView.setOnLongClickListener {
                helper.vibrateDevice()
                val selectedEvent = timeEvent[adapterPosition]
                // Delete selected event
                val deletePersonalTask = DeleteTimeEventFragment(selectedEvent)
                deletePersonalTask.show(activity.supportFragmentManager, "DeleteSelectedTimeEvent")
                true
            }
        }
    }

    override fun onBindViewHolder(holder: TimeEventViewHolder, position: Int) {
        with(holder) {
            val currentTimeEvent = timeEvent[position]
            // Set up appearance
            switcher.isChecked = currentTimeEvent.eventIsCompleted!!
            nameTextView.text = currentTimeEvent.eventName
            holder.itemView.setOnClickListener {
                onClickListener?.onClick(currentTimeEvent)
            }
        }
    }

    private fun updateEventInDatabase(timeEvent: TimeEvent) {
        firebaseManager = FirebaseManager()
        ref.child("TimeEvents").child(timeEvent.uniqueId).child("eventIsCompleted")
            .setValue(timeEvent.eventIsCompleted)
    }

    override fun getItemCount(): Int {
        return timeEvent.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setTimeEvent(timeEventList: List<TimeEvent>, targetDate: String) {
        timeEvent = timeEventList.filter { it.eventDate == targetDate }.reversed()
        notifyDataSetChanged()
    }


    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(currentTimeEvent: TimeEvent)
    }
}
