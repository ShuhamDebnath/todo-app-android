package com.example.todos3.adapters

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.todos3.databinding.EachTodoItemBinding
import com.example.todos3.models.Todo

class TodoAdapter(private val onItemClicked: OnItemClicked) :
    RecyclerView.Adapter<TodoAdapter.ViewGroup>() {
    private var recyclerTodoList = ArrayList<Todo>()
    private lateinit var binding: EachTodoItemBinding

    fun updateList(list: ArrayList<Todo>) {
        Log.d("TAG", "updateList: ")
        recyclerTodoList.clear()
        recyclerTodoList.addAll(list)
        notifyDataSetChanged()
    }

    fun removeItem(todo: Todo, position: Int) {
        recyclerTodoList.remove(todo)
        notifyItemRemoved(position)
    }

    fun removeAllItem() {
        val size = recyclerTodoList.size
        recyclerTodoList.clear()
        notifyItemRangeRemoved(0, size)
    }


    class ViewGroup(binding: EachTodoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val topicData: TextView = binding.tvTitle
        private val dateData: TextView = binding.tvDate
        val delete: ImageView = binding.ivDelete
        val layout: ConstraintLayout = binding.constraintLayout
        val cheekbox: CheckBox = binding.checkBox

        fun bind(todo: Todo, onItemClicked: OnItemClicked, position: Int) {
            var priority = "4"
            topicData.text = todo.task
            dateData.text = todo.date
            if (todo.priority != null) {
                priority = todo.priority
            }
            cheekbox.isChecked = todo.completed
            setColor(priority, layout,todo.completed)
            Log.d("TAG", "bind: cheekbox.isChecked ${cheekbox.isChecked}")
            cheekbox.setOnClickListener {
                todo.completed = !todo.completed
                Log.d("TAG", "bind: cheekbox.isChecked ${cheekbox.isChecked}")
                Log.d("TAG", "bind: position $position ")
                Log.d("TAG", "bind: todo.completed ${todo.completed}")
                if(cheekbox.isChecked){

                    onItemClicked.onCheekedClicked(todo, position)
                    layout.setBackgroundColor(Color.GREEN)
                }else{
                    onItemClicked.onCheekedClicked(todo, position)
                    setColor(priority,layout,todo.completed)
                }
//                if(!cheekbox.isChecked){
//                    todo.completed = true
//                    onItemClicked.onCheekedClicked(todo, position)
//                    setColor(priority,layout,todo.completed)
//                }else{
//                    todo.completed = false
//                    onItemClicked.onCheekedClicked(todo, position)
//                    layout.setBackgroundColor(Color.GREEN)
//                }
            }
        }


        fun setColor(priority: String, layout: ConstraintLayout, completed: Boolean) {
            if(completed){
                layout.setBackgroundColor(Color.GREEN)
                return
            }
            when (priority) {
                "1" -> layout.setBackgroundColor(Color.RED)
                "2" -> layout.setBackgroundColor(Color.parseColor("#F77B00"))
                "3" -> layout.setBackgroundColor(Color.YELLOW)
                "4" -> layout.setBackgroundColor(Color.WHITE)
            }
        }
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewGroup {
        binding = EachTodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewGroup(binding)
    }

    override fun onBindViewHolder(holder: ViewGroup, position: Int) {
        val todo = recyclerTodoList[position]
        holder.bind(todo,onItemClicked,position)
        holder.itemView.setOnClickListener {
            onItemClicked.onItemClicked(todo, position)
        }

        holder.delete.setOnClickListener {
            onItemClicked.onDeleteClicked(todo, position)
        }

    }

    override fun getItemCount(): Int {
        return recyclerTodoList.size
    }

    interface OnItemClicked {
        fun onItemClicked(todo: Todo, position: Int)
        fun onDeleteClicked(todo: Todo, position: Int)
        fun onCheekedClicked(todo: Todo, position: Int)
    }


}