package by.off.photomap.presentation.ui.login

import android.arch.lifecycle.Observer
import android.content.Intent
import android.databinding.BindingAdapter
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import by.off.photomap.R
import by.off.photomap.core.ui.BaseActivity
import by.off.photomap.core.ui.ErrorDescriptions
import by.off.photomap.databinding.ScreenSplashBinding
import by.off.photomap.di.LoginScreenComponent
import by.off.photomap.model.UserInfo
import by.off.photomap.presentation.ui.MainActivity
import by.off.photomap.presentation.viewmodel.login.LoginViewModel
import kotlinx.android.synthetic.main.dialog_login.view.*

class SplashActivity : BaseActivity<LoginViewModel, ScreenSplashBinding>() {
    companion object {
        private const val TAG_DIALOG_REGISTER = "tag_dialog_register"
    }

    override val layout: Int
        get() = R.layout.screen_splash

    override val viewModelClass: Class<LoginViewModel>
        get() = LoginViewModel::class.java

    override fun inject() {
        LoginScreenComponent.get(this).inject(this)
    }

   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.model = viewModel

        viewModel.liveData.observe(this, Observer { response ->
            if (response?.data != null)
                onUserLogged()
        })
    }

    override fun onStart() {
        super.onStart()

        viewModel.logIn()
    }

    private fun onUserLogged() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun authenticate(userName: String, pwd: String) {
        viewModel.authenticate(userName, pwd)
    }

    private fun registerUser(user: UserInfo, pwd: String) { // TODO enable to edit previous input if registration failed
        viewModel.registerAndLogin(user, pwd)
    }

    fun startLoginDialog(v: View) {
        val viewDialog = LayoutInflater.from(this).inflate(R.layout.dialog_login, null)
        AlertDialog.Builder(this)
            .setTitle(R.string.btn_login)
            .setView(viewDialog)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                authenticate(viewDialog.inputUserName.text.toString(), viewDialog.inputPwd.text.toString())
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    fun startRegisterDialog(v: View) {
        val dialog = RegisterDialogFragment()
        dialog.submitListener = { userInfo, pwd ->
            registerUser(userInfo, pwd)
        }
        dialog.show(supportFragmentManager, TAG_DIALOG_REGISTER)
    }

}

@BindingAdapter("exception")
fun setErrorMessage(textView: TextView, e: Exception?) {
    val ctx = textView.context
    val errorMsg = e?.let { ErrorDescriptions.getDescripitionRes(ctx, e) }

    textView.text = errorMsg
}