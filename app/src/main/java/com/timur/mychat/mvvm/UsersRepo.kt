package com.timur.mychat.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.timur.mychat.Utils
import com.timur.mychat.model.Users

class UsersRepo {
    private val firestore = FirebaseFirestore.getInstance()
    fun getUsers(): LiveData<List<Users>> {

        val users = MutableLiveData<List<Users>>()

        firestore.collection("Users").addSnapshotListener { snapshot, exception ->

            if (exception != null) {
                return@addSnapshotListener
            }
            val usersList = mutableListOf<Users>()
            snapshot?.documents?.forEach { document ->
                val user = document.toObject(Users::class.java)
                if (user!!.userid != Utils.getUidLoggedIn()) {
                    user.let {
                        usersList.add(it)
                    }
                }
                users.value = usersList
            }
        }
        return users
    }
}