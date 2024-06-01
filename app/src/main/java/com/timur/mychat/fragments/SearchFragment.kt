package com.timur.mychat.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.timur.mychat.R
import com.timur.mychat.adapter.OnItemClickListener
import com.timur.mychat.adapter.UserAdapter
import com.timur.mychat.databinding.FragmentSearchBinding
import com.timur.mychat.model.Users
import com.timur.mychat.mvvm.ChatAppViewModel

class SearchFragment : Fragment(), OnItemClickListener {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: UserAdapter
    private lateinit var rvUsers : RecyclerView
    private lateinit var viewModel : ChatAppViewModel
    private lateinit var nick: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Вызывается перед тем, как текст изменится
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Вызывается, когда изменяется текст
                nick = s.toString()
                check()
            }

            override fun afterTextChanged(s: Editable?) {
                // Вызывается после изменения текста
                nick = s.toString()

            }
        })

    }

    fun check(){
        val usersList = mutableSetOf<Users>()
        rvUsers = binding.recyclerSearchResults
        adapter = UserAdapter()
        val layoutManager = LinearLayoutManager(activity)
        rvUsers.layoutManager = layoutManager
        viewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)
        viewModel.getUsers().observe(viewLifecycleOwner, Observer { users ->
            Log.d("Users", "$users, $nick")
            if (users.isNotEmpty()) {
                // Очищаем текущий список пользователей
                usersList.clear()
                // Фильтруем и добавляем пользователей в список
                for (user in users) {
                    if (user.nickname?.contains(nick, ignoreCase = true) == true && nick.isNotEmpty()) {
                        usersList.add(user)
                    }
                }
                // Устанавливаем список пользователей в адаптер
                adapter.setList(usersList.toList())
                rvUsers.adapter = adapter
            }
        })
        adapter.setOnClickListener(this)
    }
    override fun onUserSelected(position: Int, users: Users) {
        val action = SearchFragmentDirections.actionSearchFragmentToChatFragment(users)
        view?.findNavController()?.navigate(action)
    }
}














