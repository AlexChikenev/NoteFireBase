package com.example.notefirebase.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.notefirebase.R
import com.example.notefirebase.firebasemodel.Project
import com.example.notefirebase.fragments.create.note.and.projects.DeleteProjectFragment
import com.example.notefirebase.utils.Helper

class ProjectAdapter(private val activity: FragmentActivity) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    private var projects: List<Project> = emptyList()
    private var onClickListener: OnClickListener? = null
    private lateinit var helper: Helper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_project, parent, false)
        helper = Helper(activity)
        return ProjectViewHolder(itemView)
    }

    inner class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.projectName)
        init {
            // Set long click listener
            itemView.setOnLongClickListener {
                helper.vibrateDevice()
                // Delete selected task
                val selectedProject = projects[adapterPosition]
                val deleteProject = DeleteProjectFragment(selectedProject)
                deleteProject.show(activity.supportFragmentManager, "DeleteSelectedProject")
                true
            }
        }
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val currentProject = projects[position]
        holder.nameTextView.text = currentProject.name

        holder.itemView.setOnClickListener {
            onClickListener?.onClick(currentProject)
        }
    }

    override fun getItemCount(): Int {
        return projects.size
    }

    fun setProjects(projectList: List<Project>) {
        projects = projectList.reversed()
        notifyDataSetChanged()
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(project: Project)
    }
}
