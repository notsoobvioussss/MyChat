package com.timur.mychat.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.timur.mychat.R
import com.timur.mychat.SignInActivity
import com.timur.mychat.adapter.OnItemClickListener
import com.timur.mychat.adapter.RecentChatAdapter
import com.timur.mychat.adapter.UserAdapter
import com.timur.mychat.adapter.onChatClicked
import com.timur.mychat.databinding.FragmentHomeBinding
import com.timur.mychat.model.RecentChats
import com.timur.mychat.model.Users
import com.timur.mychat.mvvm.ChatAppViewModel
import de.hdodenhof.circleimageview.CircleImageView

class HomeFragment : Fragment(), onChatClicked {

    lateinit var adapter: UserAdapter
    lateinit var rvRecentChats : RecyclerView
    lateinit var viewModel: ChatAppViewModel
    lateinit var toolbar: Toolbar
    lateinit var circleImageView: CircleImageView
    lateinit var firestore: FirebaseFirestore
    lateinit var recentadapter : RecentChatAdapter
    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }
    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.imageUrl.observe(viewLifecycleOwner, Observer {
            Glide.with(requireContext()).load(it).into(binding.tlImage)
        })
        recentadapter = RecentChatAdapter()
        rvRecentChats = view.findViewById(R.id.rvRecentChats)
        val layoutManager = LinearLayoutManager(activity)
        rvRecentChats.layoutManager = layoutManager
        viewModel.getRecentUsers().observe(viewLifecycleOwner, Observer {
            recentadapter.setList(it)
            rvRecentChats.adapter = recentadapter
        })
        recentadapter.setOnChatClickListener(this)
        binding.tlImage.setOnClickListener {
            view.findNavController().navigate(R.id.action_homeFragment_to_settingFragment)
        }
        binding.search.setOnClickListener{
            view.findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }


    override fun getOnChatCLickedItem(position: Int, chatList: RecentChats) {
        val action = HomeFragmentDirections.actionHomeFragmentToChatFromHomeFragment(chatList)
        view?.findNavController()?.navigate(action)
    }

}
