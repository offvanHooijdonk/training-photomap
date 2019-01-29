package by.off.photomap.presentation.ui.login

import android.arch.lifecycle.Observer
import android.content.Intent
import android.databinding.BindingAdapter
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import by.off.photomap.R
import by.off.photomap.core.ui.BaseActivity
import by.off.photomap.core.utils.di.ViewModelFactory
import by.off.photomap.databinding.ScreenSplashBinding
import by.off.photomap.di.LoginScreenComponent
import by.off.photomap.model.UserInfo
import by.off.photomap.presentation.ui.MainActivity
import by.off.photomap.presentation.viewmodel.login.LoginViewModel
import by.off.photomap.storage.parse.AuthenticationFailedException
import by.off.photomap.storage.parse.RegistrationFailedException
import by.off.photomap.storage.parse.RegistrationFailedException.Field
import by.off.photomap.storage.parse.UserNotFoundException
import kotlinx.android.synthetic.main.dialog_login.view.*
import javax.inject.Inject

class SplashActivity : BaseActivity<ScreenSplashBinding>() {
    companion object {
        private const val TAG_DIALOG_REGISTER = "tag_dialog_register"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel
        get() = viewModelFactory.create(LoginViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LoginScreenComponent.get(this).inject(this)

        val binding = DataBindingUtil.setContentView<ScreenSplashBinding>(this, R.layout.screen_splash)
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

@BindingAdapter("android:text") // TODO make a separate class that just creates string from exception
fun setErrorMessage(textView: TextView, e: Exception?) {
    val ctx = textView.context
    val errorMsg = when (e) {
        null -> null
        is AuthenticationFailedException -> ctx.getString(by.off.photomap.core.ui.R.string.error_auth_failed, e.userName)
        is UserNotFoundException -> ctx.getString(by.off.photomap.core.ui.R.string.error_user_not_found, e.id)
        is RegistrationFailedException ->
            when (e.fieldDuplicated) {
                Field.EMAIL -> ctx.getString(by.off.photomap.core.ui.R.string.error_registration_failed_by_email, e.value)
                Field.USER_NAME -> ctx.getString(by.off.photomap.core.ui.R.string.error_registration_failed_by_name, e.value)
            }
        else -> ctx.getString(by.off.photomap.core.ui.R.string.error_default_auth)
    }

    textView.text = errorMsg
}