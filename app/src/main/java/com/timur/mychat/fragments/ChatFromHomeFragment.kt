package com.timur.mychat.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.inflate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.toObject
import com.timur.mychat.R
import com.timur.mychat.Utils
import com.timur.mychat.adapter.MessageAdapter
import com.timur.mychat.databinding.FragmentChatfromHomeBinding
import com.timur.mychat.model.Messages
import com.timur.mychat.model.Users
import com.timur.mychat.mvvm.ChatAppViewModel
import de.hdodenhof.circleimageview.CircleImageView


class ChatFromHomeFragment : Fragment() {
    lateinit var args : ChatFromHomeFragmentArgs
    lateinit var binding: FragmentChatfromHomeBinding
    lateinit var viewModel : ChatAppViewModel
    lateinit var toolbar: Toolbar
    lateinit var adapter : MessageAdapter
    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = inflate(inflater, R.layout.fragment_chatfrom_home, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = ChatFromHomeFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = view.findViewById(R.id.toolBarChat)
        val circleImageView = toolbar.findViewById<CircleImageView>(R.id.chatImageViewUser)
        val textViewName = toolbar.findViewById<TextView>(R.id.chatUserName)
        args = ChatFromHomeFragmentArgs.fromBundle(requireArguments())
        viewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        Glide.with(view.getContext()).load(args.recentchats.friendsimage!!).placeholder(R.drawable.person).dontAnimate().into(circleImageView);
        textViewName.setText(args.recentchats.name)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore.collection("Users").document(args.recentchats.friendid!!).addSnapshotListener{value, error ->
            if(error!=null){
                return@addSnapshotListener
            }
            if(value != null && value.exists()){
                val userModel = value.toObject(Users::class.java)
                binding.chatUserStatus.setText(userModel!!.status.toString())
            }
        }
        binding.chatUserStatus.setText(args.recentchats.status)
        binding.chatBackBtn.setOnClickListener {
            view.findNavController().navigate(R.id.action_chatFromHomeFragment_to_homeFragment)
        }

        binding.editTextMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                // Вызывается перед тем, как текст изменится
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Вызывается, когда изменяется текст
                if(binding.editTextMessage.text.isEmpty()){
                    firestore.collection("Users").document(Utils.getUidLoggedIn()).update("status", "Online")
                }
                else {
                    firestore.collection("Users").document(Utils.getUidLoggedIn())
                        .update("status", "Typing...")
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Вызывается после изменения текста
            }
        })

        binding.sendBtn.setOnClickListener {
            if(binding.editTextMessage.text.isNotEmpty()) {
                viewModel.sendMessage(
                    Utils.getUidLoggedIn(),
                    args.recentchats.friendid!!,
                    args.recentchats.name!!,
                    args.recentchats.friendsimage!!
                )
            }
        }
        viewModel.getMessages(args.recentchats.friendid!!).observe(viewLifecycleOwner, Observer {
            initRecyclerView(it)
        })
    }

    private fun initRecyclerView(list: List<Messages>) {
        adapter = MessageAdapter()
        val layoutManager = LinearLayoutManager(context)
        binding.messagesRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        adapter.setList(list)
        adapter.notifyDataSetChanged()
        binding.messagesRecyclerView.adapter = adapter
    }
}