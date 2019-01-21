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
import by.off.photomap.di.LoginScreenComponent
import com.parse.ParseUser
import kotlinx.android.synthetic.main.dialog_login.view.*
import kotlinx.android.synthetic.main.screen_splash.*
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_splash)

        LoginScreenComponent.get(this).inject(this)
    }

    override fun onStart() {
        super.onStart()

        // todo move to utils: return null if not logged, throw exception if logged but not found
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
            // TODO move to utils
            getViewModel().getUser(parseUser.objectId).observe(this, Observer { user ->
                // todo store in session object
                if (user != null) {
                    onUserLogged()
                } else {
                    showError(true, "The logged in user '${parseUser.username}' does not exist")
                    showLoginButtons(true)
                }
            })
        }
    }

    private fun onUserLogged() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun authenticate(userName: String, pwd: String) {
        showProgress(true)
        showLoginButtons(false)

        getViewModel().authenticate(userName, pwd).observe(this, Observer { user ->
            if (user != null) {
                Log.i(LOGCAT, "User found! ${user.userName} , ${user.email}")
                onUserLogged()
            } else {
                Log.w(LOGCAT, "User was not authenticated")
                showError(true, "Could not authenticate user")
                showLoginButtons(true)
            }
            showProgress(false)
        })

    }


    private fun startLoginDialog() {
        val viewDialog = LayoutInflater.from(this).inflate(R.layout.dialog_login, null)
        AlertDialog.Builder(this)
            .setView(viewDialog)
            .setPositiveButton(android.R.string.ok) { dialog, which ->
                showLoginButtons(false)
                authenticate(viewDialog.inputUserName.text.toString(), viewDialog.inputPwd.text.toString())
            }
            .setNegativeButton(android.R.string.cancel, null) // TODO can remove?
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

    private fun getViewModel() = viewModelFactory.create(LoginViewModel::class.java)

}