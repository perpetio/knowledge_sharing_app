package com.perpetio.knowledgesharingapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.paris.girl.easycook.utils.PrefUtils
import com.perpetio.knowledgesharingapp.activity.CreateUserActivity
import com.perpetio.knowledgesharingapp.activity.PreviewProfileActivity
import com.perpetio.knowledgesharingapp.adapter.UserAdapter
import com.perpetio.knowledgesharingapp.databinding.FragmentUsersBinding
import com.perpetio.knowledgesharingapp.model.User
import com.perpetio.knowledgesharingapp.utils.Const
import com.perpetio.knowledgesharingapp.viewmodel.ViewModelState
import com.perpetio.knowledgesharingapp.viewmodel.UserViewModel
import kotlinx.coroutines.flow.collect

class UsersFragment : BaseFragment<FragmentUsersBinding>() {
    val viewModel: UserViewModel by activityViewModels()
    val adapter: UserAdapter by lazy {
        UserAdapter(userClickListener)
    }

    val userClickListener: UserAdapter.UserClickListener = object : UserAdapter.UserClickListener {
        override fun userClicked(user: User) {
            val intent = Intent(requireContext(), PreviewProfileActivity::class.java)
            intent.putExtra(Const.KEY_USER_ID, user.id)
            startActivity(intent)
        }

    }

    override fun onStart() {
        super.onStart()
        val user = PrefUtils.with(requireContext()).getUser()
        user?.company?.let { viewModel.getCompanyUsers(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setObservers()
    }

    private fun setupUI() {
        binding.apply {
            recyclerview.layoutManager = LinearLayoutManager(requireContext())
            recyclerview.adapter = adapter
            btnCreateUser.setOnClickListener {
                startActivity(Intent(requireContext(), CreateUserActivity::class.java))
            }
        }
    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                when (it) {
                    ViewModelState.Loading -> showProgress()
                    is ViewModelState.LoadedList -> showList(it.list as List<User>)
                    is ViewModelState.Error -> showToast(it.message)
                }
            }
        }
    }

    private fun showList(list: List<User>) {
        hideProgress()
        adapter.submitList(filterList(list))
    }

    private fun filterList(list: List<User>): List<User> {
        val currentUser: User? = PrefUtils.with(requireContext()).getUser()
        val resultList: MutableList<User> = mutableListOf()
        for (user in list) {
            if (!currentUser?.id.equals(user.id)) {
                resultList.add(user)
            }
        }
        return resultList
    }


    override fun getViewBinding(): FragmentUsersBinding {
        return FragmentUsersBinding.inflate(layoutInflater)
    }
}