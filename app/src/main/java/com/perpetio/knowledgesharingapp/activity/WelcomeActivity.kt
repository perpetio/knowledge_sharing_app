package com.perpetio.knowledgesharingapp.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.paris.girl.easycook.utils.PrefUtils
import com.perpetio.knowledgesharingapp.R
import com.perpetio.knowledgesharingapp.databinding.ActivityWelcomeBinding

class WelcomeActivity : BaseActivity<ActivityWelcomeBinding>(ActivityWelcomeBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = PrefUtils.with(this).getUser()
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            btnCompanyRegistration.setOnClickListener {
                val intent = Intent(this@WelcomeActivity, CompanyRegistrationActivity::class.java)
                startActivityNewTask(intent)
            }

            btnUserRegistration.setOnClickListener {
                val intent = Intent(this@WelcomeActivity, EmployeeRegistrationActivity::class.java)
                startActivityNewTask(intent)
            }

            btnLogin.setOnClickListener {
                val intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
                startActivityNewTask(intent)
            }
        }

    }

    override fun getViewBinding(): ActivityWelcomeBinding {
        return ActivityWelcomeBinding.inflate(layoutInflater)
    }
}