package by.off.photomap.presentation.ui.login

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import by.off.photomap.R
import by.off.photomap.core.ui.hide
import by.off.photomap.core.ui.show
import by.off.photomap.core.utils.LOGCAT
import by.off.photomap.core.utils.di.ViewModelFactory
import by.off.photomap.core.utils.session.Session
import by.off.photomap.di.LoginScreenComponent
import by.off.photomap.model.UserInfo
import by.off.photomap.storage.parse.AuthenticationFailedException
import by.off.photomap.storage.parse.UserNotFoundException
import com.parse.ParseUser
import kotlinx.android.synthetic.main.dialog_login.view.*
import kotlinx.android.synthetic.main.screen_splash.*
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: LoginViewModel
        get() = viewModelFactory.create(LoginViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_splash)

        LoginScreenComponent.get(this).inject(this)

        btnLogin.setOnClickListener { startLoginDialog() }
        btnRegister.setOnClickListener { }

        viewModel.logIn().observe(this, Observer { response ->
            val user = response?.data
            when {
                response?.error != null -> {
                    showError(true, "Error logging in with the current user")
                    showLoginButtons(true)
                }
                user == null -> {
                    showLoginButtons(true)
                }
                else -> {
                    onUserLogged(user)
                }
            }
        })
    }

    private fun onUserLogged(user: UserInfo) {
        Session.user = user
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun authenticate(userName: String, pwd: String) {
        showProgress(true)
        showLoginButtons(false)
        showError(false)

        viewModel.authenticate(userName, pwd).observe(this, Observer { response ->
            if (response?.error != null) {
                Log.w(LOGCAT, "User was not authenticated", response.error)
                showError(true, "Could not authenticate user $userName")
                showLoginButtons(true)
            } else {
                Log.i(LOGCAT, "User found! ${response!!.data!!.userName} , ${response.data!!.email}")
                showProgress(false)
                onUserLogged(response.data!!)
            }
        })
    }

    private fun startLoginDialog() {
        val viewDialog = LayoutInflater.from(this).inflate(R.layout.dialog_login, null)
        AlertDialog.Builder(this)
            .setTitle(R.string.login_btn)
            .setView(viewDialog)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                showLoginButtons(false)
                authenticate(viewDialog.inputUserName.text.toString(), viewDialog.inputPwd.text.toString())
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun showLoginButtons(isShow: Boolean) {
        if (isShow) {
            btnLogin.show()
            btnRegister.show()
        } else {
            btnLogin.hide()
            btnRegister.hide()
        }
    }

    private fun showProgress(isShow: Boolean) {
        if (isShow) {
            progressLogin.show()
        } else {
            progressLogin.hide()
        }
    }

    private fun showError(isShow: Boolean, text: String = "") {
        if (isShow) {
            showProgress(false)
            txtError.text = text
            txtError.show()
        } else {
            txtError.hide()
        }
    }

}