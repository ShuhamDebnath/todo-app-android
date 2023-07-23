package com.example.todos3.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import com.example.todos3.R
import com.example.todos3.databinding.FragmentEditBinding
import com.example.todos3.interfaces.ApiService
import com.example.todos3.util.RetrofitHelper
import com.example.todos3.viewModels.ViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class EditFragment : BottomSheetDialogFragment() {

    private lateinit var context: Context
    private lateinit var binding: FragmentEditBinding

    private lateinit var buttonDone: Button
    private lateinit var topic: EditText

    private lateinit var colorLayout: LinearLayout
    private lateinit var pryority: LinearLayout
    lateinit var task: TextInputEditText

    private lateinit var apiService: ApiService
    private val viewModel: ViewModel by activityViewModels()

    var priority: String = "3"


    override fun onAttach(context: Context) {
        this.context = context
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditBinding.inflate(inflater, container, false)
        buttonDone = binding.btnDone
        pryority = binding.llPryority
        task = binding.etTask

        if (viewModel.getIsUpdate()) {
            val todo = viewModel.getTempTodo()
            task.setText(todo.task)
            setPriorityDisable()
            when (todo.priority) {
                "1" -> binding.btnDone.setBackgroundColor(Color.RED)
                "2" -> binding.btnDone.setBackgroundColor(resources.getColor(R.color.orange))
                "3" -> binding.btnDone.setBackgroundColor(resources.getColor(R.color.yellow))
                "4" -> binding.btnDone.setBackgroundColor(Color.WHITE)
            }
        }

        apiService = RetrofitHelper.getInstance().create(ApiService::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonDone.setOnClickListener {
            saveTask()
            dismiss()
        }
//        setColor()
        setPriority()
    }

    private fun saveTask() {
        val task = task.text.toString().trim()
        val date = getCurrentDate()
        val completed = false
        if (task.isNotEmpty()) {
            val body = JsonObject().apply {
                addProperty("task", task)
                addProperty("priority", priority)
                addProperty("date", date)
                addProperty("completed", completed)
            }

            if (viewModel.getIsUpdate()) {
                viewModel.setIsUpdate(false)
                val id = viewModel.getTempTodo()._id.toString()
                viewModel.updateUserById(id, body)
            } else {
                viewModel.createUser(body)
            }
        }
    }

    private fun setPriority() {
        binding.white.setOnClickListener {
            setPriorityDisable()
            binding.btnDone.setBackgroundColor(resources.getColor(R.color.white))
//            binding.white.setBackgroundResource(R.drawable.selected_circle)
            priority = "4"
        }
        binding.yellow.setOnClickListener {
            setPriorityDisable()
            binding.btnDone.setBackgroundColor(resources.getColor(R.color.yellow))
            priority = "3"
        }
        binding.orange.setOnClickListener {
            setPriorityDisable()
            binding.btnDone.setBackgroundColor(resources.getColor(R.color.orange))
            priority = "2"
        }
        binding.red.setOnClickListener {
            setPriorityDisable()
            binding.btnDone.setBackgroundColor(resources.getColor(R.color.red))
            priority = "1"
        }
    }

    private fun setPriorityDisable() {
//        binding.one.setBackgroundResource(R.drawable.un_selected_circle)
//        binding.two.setBackgroundResource(R.drawable.un_selected_circle)
//        binding.three.setBackgroundResource(R.drawable.un_selected_circle)
    }

    private fun setColor() {
//        binding.white.setOnClickListener {
//            binding.root.setBackgroundColor(Color.WHITE)
//            color = "white"
//        }
//        binding.red.setOnClickListener {
//            binding.root.setBackgroundColor(Color.RED)
//            color = "red"
//        }
//        binding.green.setOnClickListener {
//            binding.root.setBackgroundColor(Color.GREEN)
//            color = "green"
//        }
//        binding.blue.setOnClickListener {
//            binding.root.setBackgroundColor(Color.BLUE)
//            color = "blue"
//        }
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val currentDate = Date()
        return sdf.format(currentDate)
    }

    private fun setImageViewClickListener(linearLayout: LinearLayout) {
        for (i in 0 until linearLayout.childCount) {
            val imageView = linearLayout.getChildAt(i) as ImageView
            imageView.setOnClickListener {
                val imageId = imageView.id
                Log.d("TAG", "setImageViewClickListener: ${imageId}")
            }
        }
    }


}