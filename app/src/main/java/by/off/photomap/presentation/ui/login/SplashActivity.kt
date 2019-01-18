package by.off.photomap.presentation.ui.login

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import by.off.photomap.R
import by.off.photomap.core.ui.hide
import by.off.photomap.core.ui.show
import by.off.photomap.core.utils.LOGCAT
import by.off.photomap.storage.parse.impl.UserServiceImpl
import com.parse.ParseUser
import kotlinx.android.synthetic.main.dialog_login.view.*
import kotlinx.android.synthetic.main.screen_splash.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.screen_splash)
    }

    override fun onStart() {
        super.onStart()

        val parseUser: ParseUser? = ParseUser.getCurrentUser()
        if (parseUser == null) {
            Log.i(LOGCAT, "No logged user")
            showLoginButtons(true)
            btnLogin.setOnClickListener {
                startLoginDialog()
            }
            btnRegister.setOnClickListener {

            }
        } else {
            Log.i(LOGCAT, "Logged User - ${parseUser.objectId} , ${parseUser.username}")
            onUserLogged()
        }


    }

    private fun onUserLogged() {
        // TODO place a user into Session object
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun authenticate(userName: String, pwd: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val userService = UserServiceImpl()
            showProgress(true)
            userService.authenticate(userName, pwd)
                .observe(this@SplashActivity, Observer { user ->
                    if (user != null) {
                        Log.i(LOGCAT, "User found! ${user.userName} , ${user.email}")
                        onUserLogged()
                    } else {
                        Log.w(LOGCAT, "Sorry no user")
                        Toast.makeText(this@SplashActivity, "Sorry no user", Toast.LENGTH_LONG).show()
                    }
                    showProgress(false)
                })
        }

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

    private fun startLoginDialog() {
        val viewDialog = LayoutInflater.from(this).inflate(R.layout.dialog_login, null)
        AlertDialog.Builder(this)
            .setView(viewDialog)
            .setPositiveButton(android.R.string.ok) { dialog, which ->
                showLoginButtons(false)
                authenticate(viewDialog.inputUserName.text.toString(), viewDialog.inputPwd.text.toString())
            }
            .setNegativeButton(android.R.string.cancel) { dialog, which -> dialog.dismiss() } // TODO can remove?
            .show()
    }

    private fun showProgress(isShow: Boolean) {
        if (isShow) {
            progressLogin.show()
        } else {
            progressLogin.hide()
        }
    }
}