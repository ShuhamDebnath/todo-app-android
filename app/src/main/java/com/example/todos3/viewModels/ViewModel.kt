package com.example.todos3.viewModels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.todos3.interfaces.ApiService
import com.example.todos3.models.Todo
import com.example.todos3.repositories.Repository
import com.example.todos3.util.RetrofitHelper
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class ViewModel(private val application: Application) : AndroidViewModel(application) {

    private val todoList = MutableLiveData<ArrayList<Todo>>()
    private val apiService = RetrofitHelper.getInstance().create(ApiService::class.java)
    private val repository = Repository(apiService)
    private var tempTodo = Todo(null, "", null, null, false, null)
    private var isUpdate = false;
    private var position = 0;

    private var searchQuery: String = ""
    private var filteredList: List<Todo> = ArrayList()

    init {
        getAllUser()
    }

    fun setPosition(position: Int) {
        this.position = position
        Log.d("TAG", "setPosition: $position")
    }

    fun getPosition(): Int {
        Log.d("TAG", "getIsUpdate: $position")
        return position
    }

    fun setIsUpdate(isUpdate: Boolean) {
        this.isUpdate = isUpdate
        Log.d("TAG", "setIsUpdate: $isUpdate")
    }

    fun getIsUpdate(): Boolean {
        Log.d("TAG", "getIsUpdate: $isUpdate")
        return isUpdate

    }

    fun setTempTodo(todo: Todo) {
        tempTodo = todo
    }

    fun getTempTodo(): Todo {
        return tempTodo
    }

    fun setSearchQuery(query: String) {
        searchQuery = query
    }

    fun getSearchQuery(): String {
        return searchQuery
    }

    fun setFilteredList(list: List<Todo>?) {
        if (list != null) {
            filteredList = list
        }
    }

    fun getFilteredList(): List<Todo> {
        return filteredList
    }

    fun getTodoList(): MutableLiveData<ArrayList<Todo>> {
        return todoList
    }

    fun addToTodoList(list: ArrayList<Todo>) {
        Log.d("TAG", "addToTodoList: ")
        var currentList = todoList.value
        if (currentList == null) {
            currentList = list
        } else {
            currentList.addAll(list);
        }
        viewModelScope.launch(Dispatchers.Main) {
            todoList.value = currentList!!
        }
    }

    fun addToTodoList(list: ArrayList<Todo>, position: Int) {
        Log.d("TAG", "addToTodoList: ")
        var currentList = todoList.value
        if (currentList == null) {
            currentList = list
        } else {
            currentList.addAll(position, list);
            currentList.removeAt(position + 1)
        }
        viewModelScope.launch(Dispatchers.Main) {
            todoList.value = currentList!!
        }
    }


    fun searchTask(searchText: String): List<Todo>? {
        Log.d("TAG", "searchTask: ")
        val filteredList = todoList.value?.filter { todo ->
            todo.task.toLowerCase(Locale.ROOT).contains(searchText.toLowerCase(Locale.ROOT))
        }
        return filteredList
    }


    fun getAllUser() {
        Log.d("TAG", "getAllUser: ")
        viewModelScope.launch {
            repository.getAllUser().enqueue(object : Callback<List<Todo>?> {
                override fun onResponse(call: Call<List<Todo>?>, response: Response<List<Todo>?>) {
                    if (response.isSuccessful) {
                        val list = response.body()
                        val tempList = ArrayList<Todo>()
                        Log.d("TAG", "onResponse: $list")
                        list?.forEach { todo ->
                            val tempTodo = Todo(
                                todo._id,
                                todo.task,
                                todo.priority,
                                todo.date,
                                todo.completed,
                                todo.__v
                            )
                            tempList.add(tempTodo)
                            Log.d("TAG", "onResponse: $tempTodo")
                        }
                        addToTodoList(tempList)
                        Log.d("TAG", "getAllUser: todoList ${todoList.value}")
                        Log.d("TAG", "getAllUser: todoList ${todoList.value?.size}")
                    }
                }

                override fun onFailure(call: Call<List<Todo>?>, t: Throwable) {
                    Log.d("TAG", "onFailure: ${t.localizedMessage}")
                }
            })
        }
    }

    fun getUserById(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getUserById(id)
        }
    }

    fun createUser(body: JsonObject) {
        viewModelScope.launch {
            try {
                val response = repository.createUser(body)
                if (response.isSuccessful) {
                    val jsonObject = response.body()
                    if (jsonObject != null) {
                        val todo = Todo(
                            _id = jsonObject["_id"]?.asString,
                            task = jsonObject["task"].asString,
                            priority = jsonObject["priority"]?.asString,
                            date = jsonObject["date"]?.asString,
                            completed = jsonObject["completed"]?.asBoolean ?: false,
                            __v = if (jsonObject["__v"]?.isJsonNull == true) null else jsonObject["__v"]?.asInt
                        )
                        val tempList = ArrayList<Todo>()
                        tempList.add(todo)
                        Log.d("TAG", "createUserById: success : ${todo}")
                        Toast.makeText(application, "Task Created", Toast.LENGTH_SHORT).show()
                        addToTodoList(tempList)
                        Log.d("TAG", "createUser: ${todoList.value}")
                    } else {
                        Log.d("TAG", "createUser: response.body() = null ")
                    }

                } else {
                    Log.d("TAG", "createUserById: error : ${response.message()}")
                }
            } catch (e: Error) {
                Log.d("TAG", "saveTask: ${e.localizedMessage}")
            }

        }
    }

    fun updateUserById(id: String, body: JsonObject) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("TAG", "updateUserById: ")
            val response = repository.updateUserById(id, body)
            if (response.isSuccessful) {
                val jsonObject = response.body()
                if (jsonObject != null) {
                    val todo = Todo(
                        _id = jsonObject["_id"]?.asString,
                        task = jsonObject["task"].asString,
                        priority = jsonObject["priority"]?.asString,
                        date = jsonObject["date"]?.asString,
                        completed = jsonObject["completed"]?.asBoolean ?: false,
                        __v = if (jsonObject["__v"]?.isJsonNull == true) null else jsonObject["__v"]?.asInt
                    )
                    val tempList = ArrayList<Todo>()
                    tempList.add(todo)

                    addToTodoList(tempList, position);
                    position = 0;
                    Log.d("TAG", "updateUserById: ${todo}")
                } else {
                    Log.d("TAG", "updateUserById: is null")
                }
            } else {
                Log.d("TAG", "updateUserById: ${response.message()}")
            }
        }
    }

    fun updateCompletedById(id: String, body: JsonObject) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("TAG", "updateUserById: ")
            val response = repository.updateUserById(id, body)
            if (response.isSuccessful) {
                val jsonObject = response.body()
                if (jsonObject != null) {
                    val todo = Todo(
                        _id = jsonObject["_id"]?.asString,
                        task = jsonObject["task"].asString,
                        priority = jsonObject["priority"]?.asString,
                        date = jsonObject["date"]?.asString,
                        completed = jsonObject["completed"]?.asBoolean ?: false,
                        __v = if (jsonObject["__v"]?.isJsonNull == true) null else jsonObject["__v"]?.asInt
                    )
                    val tempList = ArrayList<Todo>()
                    tempList.add(todo)

//                    addToTodoList(tempList, position);
//                    position = 0;
                    Log.d("TAG", "updateUserById: ${todo}")
                } else {
                    Log.d("TAG", "updateUserById: is null")
                }
            } else {
                Log.d("TAG", "updateUserById: ${response.message()}")
            }
        }
    }

    fun deleteUserById(id: String) {
        Log.d("TAG", "deleteUserById: ")
        viewModelScope.launch {
            repository.deleteUserById(id)
            Toast.makeText(application, "Task Deleted", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteAll()
            Toast.makeText(application, "All Task Deleted", Toast.LENGTH_SHORT).show()
        }
    }

}