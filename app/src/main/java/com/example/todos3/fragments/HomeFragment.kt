package com.example.todos3.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todos3.R
import com.example.todos3.adapters.TodoAdapter
import com.example.todos3.databinding.FragmentHomeBinding
import com.example.todos3.interfaces.ApiService
import com.example.todos3.models.Todo
import com.example.todos3.util.RetrofitHelper
import com.example.todos3.viewModels.ViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.JsonObject
import java.util.Locale

class HomeFragment : Fragment(), TodoAdapter.OnItemClicked {

    private lateinit var context: Context
    private lateinit var searchView: EditText
    private lateinit var binding: FragmentHomeBinding
    private lateinit var add: FloatingActionButton
    private lateinit var apiService: ApiService
    private val viewModel: ViewModel by activityViewModels()
    private lateinit var bottomSheetDialogFragment: BottomSheetDialogFragment
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: TodoAdapter


    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        add = binding.btnAdd
        apiService = RetrofitHelper.getInstance().create(ApiService::class.java)
        recyclerView = binding.recyclerViewHome
        searchView = binding.searchView
        adapter = TodoAdapter(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val searchText = viewModel.getSearchQuery()
        val filteredList = viewModel.getFilteredList()
        searchView.setText(searchText)
        adapter.updateList(filteredList as ArrayList<Todo>)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        add.setOnClickListener {
            viewModel.setIsUpdate(false)
            openBottomSheetFragment()
        }

        binding.textViewHeader.setOnClickListener {
            viewModel.deleteAll()
            adapter.removeAllItem()
        }

        searchView.setOnClickListener {
            searchTask()
        }

        searchView.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Call the searchTask function when the user types something in the EditText view
                Log.d("TAG", "onTextChanged: ")

                val searchText = searchView.text.toString()
                val filteredList = viewModel.searchTask(searchText)

                Log.d("TAG", "onTextChanged: $searchText ")
                // Store the search query and filtered list in the ViewModel
                viewModel.setSearchQuery(searchText)
                viewModel.setFilteredList(filteredList!!)

                adapter.updateList(filteredList as ArrayList<Todo>)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {}
        })

        viewModel.getTodoList().observe(viewLifecycleOwner) {
            Log.d("TAG", "onViewCreated: observer called")
            adapter.updateList(it)
        }

    }

    private fun searchTask() {
        val searchText = searchView.text.toString()
        val filteredList = viewModel.getTodoList().value?.filter { todo ->
            todo.task.toLowerCase(Locale.ROOT).contains(searchText.toLowerCase(Locale.ROOT))
        }
        adapter.updateList(filteredList as ArrayList)
    }

    private fun openBottomSheetFragment() {
        val bottomSheetFragment = EditFragment()
        bottomSheetFragment.isCancelable = true
        bottomSheetFragment.show(parentFragmentManager, "MyBottomSheetDialogFragment")
    }


    private fun toEditFragment() {
        findNavController().navigate(R.id.action_homeFragment_to_editFragment)
    }

    override fun onItemClicked(todo: Todo, position: Int) {
        viewModel.setTempTodo(todo)
        viewModel.setIsUpdate(true)
        viewModel.setPosition(position)
        openBottomSheetFragment()
        Log.d("TAG", "onItemClicked: ${viewModel.getTodoList().value}")
//        adapter.notifyItemRemoved(position)
//        adapter.notifyItemChanged(position)
    }

    override fun onDeleteClicked(todo: Todo, position: Int) {
        todo._id?.let { viewModel.deleteUserById(it) }
        adapter.removeItem(todo, position)
    }

    override fun onCheekedClicked(todo: Todo, position: Int) {
        Log.d("TAG", "onCheekedClicked: ")
        val body = JsonObject().apply {
            addProperty("task", todo.task)
            addProperty("priority", todo.priority)
            addProperty("date", todo.date)
            addProperty("completed", todo.completed)
        }
        val id = todo._id.toString()
        viewModel.updateCompletedById(id, body)
    }
}