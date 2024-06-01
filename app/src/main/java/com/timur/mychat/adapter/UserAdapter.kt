package com.timur.mychat.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.timur.mychat.R
import com.timur.mychat.fragments.HomeFragment
import com.timur.mychat.model.Users
import de.hdodenhof.circleimageview.CircleImageView


class UserAdapter : RecyclerView.Adapter<UserHolder>() {

    private var listOfUsers = listOf<Users>()
    private var listener: OnItemClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfUsers.size
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val users = listOfUsers[position]
        val name = users.username!!.split("\\s".toRegex())[0]
        holder.searchName.setText(name)
        if (users.status.equals("Online")){
            holder.searchStatus.text = "Online"
        } else {
            holder.searchStatus.text = "Offline"
        }
        Glide.with(holder.itemView.context).load(users.imageUrl).into(holder.searchProfile)
        holder.itemView.setOnClickListener {
            listener?.onUserSelected(position, users)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<Users>){
        this.listOfUsers = list
        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: OnItemClickListener){
        this.listener = listener
    }
}

class UserHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val searchName: TextView = itemView.findViewById(R.id.searchChatTextName)
    val searchProfile : CircleImageView = itemView.findViewById(R.id.searchChatImageView)
    val searchStatus: TextView = itemView.findViewById(R.id.seachUserStatus)
}


interface OnItemClickListener{
    fun onUserSelected(position: Int, users: Users)
}
