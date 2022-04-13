package com.perpetio.knowledgesharingapp.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.paris.girl.easycook.utils.PrefUtils
import com.perpetio.knowledgesharingapp.R
import com.perpetio.knowledgesharingapp.activity.*
import com.perpetio.knowledgesharingapp.databinding.FragmentSettingsBinding

class SettingFragment : BaseFragment<FragmentSettingsBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
    }

    override fun onStart() {
        super.onStart()
        updateUserInfo()
    }

    private fun setupButtons() {
        binding.apply {
            btnChangePassword.setOnClickListener {
                startActivity(Intent(requireContext(), ChangePasswordActivity::class.java))
            }
            btnSavedPosts.setOnClickListener {
                startActivity(Intent(requireContext(), SavePostsActivity::class.java))
            }
            btnUpdateProfile.setOnClickListener {
                startActivity(Intent(requireContext(), UpdateProfileActivity::class.java))
            }
            btnLogout.setOnClickListener {
                showLogoutDialog()
            }
        }

    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.are_you_sure)
            .setMessage(R.string.are_you_sure_logout)
            .setPositiveButton(
                R.string.yes,
                { dialogInterface, i ->
                    Firebase.auth.signOut()
                    PrefUtils.with(requireContext()).clear()
                    val intent = Intent(requireContext(), WelcomeActivity::class.java)
                    startActivityClearTask(intent)
                })
            .setNegativeButton(
                R.string.no,
                { dialogInterface, i ->
                    dialogInterface.dismiss()
                })
            .show()
    }

    private fun updateUserInfo() {
        binding.apply {
            val user = PrefUtils.with(requireContext()).getUser()
            userName.text = user?.name
            userJobTitle.text = user?.jobTitle

            Glide.with(requireContext())
                .load(user?.avatarUrl)
                .placeholder(R.drawable.user_placeholder)
                .error(R.drawable.user_placeholder)
                .circleCrop()
                .into(avatar)
        }

    }

    override fun getViewBinding(): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(layoutInflater)
    }
}