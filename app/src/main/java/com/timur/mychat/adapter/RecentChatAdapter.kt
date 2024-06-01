package com.timur.mychat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.timur.mychat.R
import com.timur.mychat.Utils
import com.timur.mychat.fragments.ChatFromHomeFragmentArgs
import com.timur.mychat.fragments.HomeFragment
import com.timur.mychat.model.RecentChats
import com.timur.mychat.model.Users
import de.hdodenhof.circleimageview.CircleImageView

class RecentChatAdapter : RecyclerView.Adapter<MyChatListHolder>() {

    var listOfChats = listOf<RecentChats>()
    private var listener: onChatClicked? = null
    var chatShitModel = RecentChats()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyChatListHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.recentchatlist, parent, false)
        return MyChatListHolder(view)


    }

    override fun getItemCount(): Int {
        return listOfChats.size
    }


    fun setList(list: List<RecentChats>) {
        this.listOfChats = list
    }

    override fun onBindViewHolder(holder: MyChatListHolder, position: Int) {
        val chatlist = listOfChats[position]
        chatShitModel = chatlist
        holder.userName.setText(chatlist.name)
        val themessage = chatlist.message!!.split(" ").take(4).joinToString(" ")
        val makelastmessage = "${chatlist.person}: ${themessage} "


        holder.lastMessage.setText(makelastmessage)

        Glide.with(holder.itemView.context).load(chatlist.friendsimage).into(holder.imageView)
        val timelist = Utils.convertToLocal(chatlist.time!!.toString()).split(":")
        holder.timeView.setText("${timelist[3]}:${timelist[4]}")

        holder.itemView.setOnClickListener {
            listener?.getOnChatCLickedItem(position, chatlist)


        }

        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Users").document(chatlist.friendid!!).addSnapshotListener{value, error ->
            if(error!=null){
                return@addSnapshotListener
            }
            if(value != null && value.exists()){
                val userModel = value.toObject(Users::class.java)
                if(userModel!!.status.toString() == "Online"){
                    holder.statusImageView.setImageResource(R.drawable.onlinestatus)
                }
            }
        }


    }


    fun setOnChatClickListener(listener: HomeFragment) {
        this.listener = listener
    }
}

class MyChatListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val statusImageView: ImageView = itemView.findViewById(R.id.statusChat)
    val imageView: CircleImageView = itemView.findViewById(R.id.recentChatImageView)
    val userName: TextView = itemView.findViewById(R.id.recentChatTextName)
    val lastMessage: TextView = itemView.findViewById(R.id.recentChatTextLastMessage)
    val timeView: TextView = itemView.findViewById(R.id.recentChatTextTime)
}


interface onChatClicked {
    fun getOnChatCLickedItem(position: Int, chatList: RecentChats)
}
