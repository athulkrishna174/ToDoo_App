package com.example.todoo.utils.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoo.databinding.ToDooItemLayoutBinding
import com.example.todoo.utils.model.ToDooData

class ToDooAdapter(private val list: MutableList<ToDooData>) :
    RecyclerView.Adapter<ToDooAdapter.ToDooViewHolder>() {

        private var listener: ToDooAdapterClicksInterface? = null

    fun setListener(listener: ToDooAdapterClicksInterface){
        this.listener = listener
    }

    inner class ToDooViewHolder(val binding: ToDooItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDooViewHolder {
        val binding = ToDooItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ToDooViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ToDooViewHolder, position: Int) {
        with(holder){
            with(list[position]){
                binding.todoTask.text = this.task

                binding.deleteTask.setOnClickListener {
                    listener?.onDeleteTaskButtonClicked(this)
                }

                binding.editTask.setOnClickListener {
                    listener?.onEditTaskButtonClicked(this)
                }
            }
        }
    }

    interface ToDooAdapterClicksInterface{
        fun onDeleteTaskButtonClicked(toDooData: ToDooData)
        fun onEditTaskButtonClicked(toDooData: ToDooData)
    }
}