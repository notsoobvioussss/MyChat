package com.timur.mychat.mvvm

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.timur.mychat.MyApplication
import com.timur.mychat.SharedPrefs
import com.timur.mychat.Utils
import com.timur.mychat.model.Messages
import com.timur.mychat.model.RecentChats
import com.timur.mychat.model.Users
import com.timur.mychat.notifications.entity.NotificationData
import com.timur.mychat.notifications.entity.PushNotification
import com.timur.mychat.notifications.entity.Token
import com.timur.mychat.notifications.network.RetrofitInstance
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatAppViewModel : ViewModel() {
    val message = MutableLiveData<String>()
    val firestore = FirebaseFirestore.getInstance()
    val name = MutableLiveData<String>()
    val nickname = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()
    val usersRepo = UsersRepo()
    val messageRepo = MessageRepo()
    var token: String? = null
    val chatlistRepo = ChatListRepo()

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }


    init {
        getCurrentUser()
        getRecentUsers()
    }

    fun getUsers(): LiveData<List<Users>> {
        return usersRepo.getUsers()
    }

    fun sendMessage(sender: String, receiver: String, friendname: String, friendimage: String) =
        viewModelScope.launch(Dispatchers.IO) {
            val context = MyApplication.instance.applicationContext
            val hashMap = hashMapOf<String, Any>(
                "sender" to sender,
                "receiver" to receiver,
                "message" to message.value!!,
                "time" to Utils.getTime()
            )

            val uniqueId = listOf(sender, receiver).sorted()
            uniqueId.joinToString(separator = "")

            val friendnamesplit = friendname.split("\\s".toRegex())[0]
            val mysharedPrefs = SharedPrefs(context)
            mysharedPrefs.setValue("friendid", receiver)
            mysharedPrefs.setValue("chatroomid", uniqueId.toString())
            mysharedPrefs.setValue("friendname", friendnamesplit)
            mysharedPrefs.setValue("friendimage", friendimage)

            firestore.collection("Messages").document(uniqueId.toString()).collection("chats")
                .document(Utils.getTime()).set(hashMap).addOnCompleteListener { taskmessage ->
                    val setHashap = hashMapOf<String, Any>(
                        "friendid" to receiver,
                        "time" to Utils.getTime(),
                        "sender" to Utils.getUidLoggedIn(),
                        "message" to message.value!!,
                        "friendsimage" to friendimage,
                        "name" to friendname,
                        "person" to "you"
                    )

                    firestore.collection("Conversation${Utils.getUidLoggedIn()}").document(receiver)
                        .set(setHashap)

                    firestore.collection("Conversation${receiver}").document(Utils.getUidLoggedIn())
                        .update(
                            "message",
                            message.value!!,
                            "time",
                            Utils.getTime(),
                            "person",
                            name.value!!
                        )

                    firestore.collection("Tokens").document(receiver).addSnapshotListener { value, error ->

                        if (value != null && value.exists()) {

                            val tokenObject = value.toObject(Token::class.java)

                            token = tokenObject?.token!!

                            val loggedInUsername =
                                mysharedPrefs.getValue("username")!!.split("\\s".toRegex())[0]

                            if (message.value!!.isNotEmpty() && receiver.isNotEmpty()) {
                                PushNotification(
                                    NotificationData(loggedInUsername, message.value!!), token!!
                                ).also {
                                    sendNotification(it)
                                }

                            } else {
                                Log.e("ChatAppViewModel", "NO TOKEN, NO NOTIFICATION")
                            }


                        }

                        Log.e("ViewModel", token.toString())

                        if (taskmessage.isSuccessful){
                            message.value = ""
                        }
                    }
                }
        }

    fun getMessages(friend: String): LiveData<List<Messages>> {

        return messageRepo.getMessages(friend)
    }

    fun getRecentUsers(): LiveData<List<RecentChats>> {
        return chatlistRepo.getAllChatList()
    }


    fun sendNotification(notification: PushNotification) = viewModelScope.launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
        } catch (e: Exception) {
            Log.e("ViewModelError", e.toString())
        }
    }


    fun getCurrentUser() = viewModelScope.launch(Dispatchers.IO) {
        val context = MyApplication.instance.applicationContext
        firestore.collection("Users").document(Utils.getUidLoggedIn())
            .addSnapshotListener { value, error ->
                if (value!!.exists() && value != null) {
                    val users = value.toObject(Users::class.java)
                    name.value = users?.username!!
                    imageUrl.value = users.imageUrl!!
                    nickname.value = users.nickname!!
                    val mysharedPrefs = SharedPrefs(context)
                    mysharedPrefs.setValue("username", users.username!!)
                    mysharedPrefs.setValue("nickname", users.nickname!!)
                }
            }
    }


    fun updateProfile() = viewModelScope.launch(Dispatchers.IO) {
        val context = MyApplication.instance.applicationContext
        val hashMapUser =
            hashMapOf<String, Any>("username" to name.value!!, "imageUrl" to imageUrl.value!!, "nickname" to nickname.value!!)
        firestore.collection("Users").document(Utils.getUidLoggedIn()).update(hashMapUser).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(context, "Updated", Toast.LENGTH_SHORT ).show()
            }
        }


        val mysharedPrefs = SharedPrefs(context)
        val friendid = mysharedPrefs.getValue("friendid")
        val hashMapUpdate = hashMapOf<String, Any>("friendsimage" to imageUrl.value!!, "name" to name.value!!, "nickname" to nickname.value!!, "person" to name.value!!)

        firestore.collection("Conversation${friendid}").document(Utils.getUidLoggedIn()).update(hashMapUpdate)
        firestore.collection("Conversation${Utils.getUidLoggedIn()}").document(friendid!!).update("person", "you")
    }
}