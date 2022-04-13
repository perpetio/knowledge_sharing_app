package com.perpetio.knowledgesharingapp.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.paris.girl.easycook.utils.PrefUtils
import com.perpetio.knowledgesharingapp.R
import com.perpetio.knowledgesharingapp.databinding.ActivityMainBinding
import com.perpetio.knowledgesharingapp.model.User
import com.perpetio.knowledgesharingapp.utils.Const
import com.perpetio.knowledgesharingapp.viewmodel.ViewModelState
import com.perpetio.knowledgesharingapp.viewmodel.UserViewModel
import kotlinx.coroutines.flow.collect

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObservers()
        inflateBottomMenu()
        PrefUtils.with(this).getUser()?.id?.let { viewModel.getUserInfo(it) }
    }

    private fun inflateBottomMenu() {
        val user = PrefUtils.with(this).getUser()
        if (user != null) {
            val type: Long? = user.type
            if (type == Const.COMPANY_TYPE) {
                binding.bottomNavigation.menu.clear()
                binding.bottomNavigation.inflateMenu(R.menu.bottom_navigation_company)
            }
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        val navController = navHostFragment?.navController
        if (navController != null) {
            NavigationUI.setupWithNavController(
                binding.bottomNavigation,
                navController
            )
        }
    }

    private fun setObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                when (it) {
                    ViewModelState.Loading -> showProgress()
                    is ViewModelState.Error -> showToast(it.message)
                    is ViewModelState.LoadedUser -> updateUser(it.user)
                }
            }
        }
    }

    private fun updateUser(user: User?) {
        PrefUtils.with(this).setUser(user)
    }

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }
}