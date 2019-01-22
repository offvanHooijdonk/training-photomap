package by.off.photomap.presentation.ui.login

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import by.off.photomap.R
import by.off.photomap.core.ui.colorError
import by.off.photomap.model.UserInfo
import kotlinx.android.synthetic.main.dialog_register.*
import kotlinx.android.synthetic.main.dialog_register.view.*

class RegisterDialogFragment : DialogFragment() {
    var submitListener: ((UserInfo, String) -> Unit)? = null

    private lateinit var validators: List<RegexValidator>
    private lateinit var ctx: Context
    private var minUserName = 0
    private var maxUserName = 0
    private var minPwd = 0
    private var maxPwd = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ctx = requireContext()
        minUserName = ctx.resources.getInteger(R.integer.min_user_name)
        maxUserName = ctx.resources.getInteger(R.integer.max_user_name)
        minPwd = ctx.resources.getInteger(R.integer.min_pwd)
        maxPwd = ctx.resources.getInteger(R.integer.max_pwd)

        validators = mutableListOf(
            RegexValidator(
                inputUserName,
                ctx.getString(R.string.pattern_username, minUserName, maxUserName),
                ctx.getString(R.string.pattern_username_error, minUserName, maxUserName)
            ),
            RegexValidator(inputEmail, ctx.getString(R.string.pattern_email), ctx.getString(R.string.pattern_email_error)),
            RegexValidator(inputPwd, ctx.getString(R.string.pattern_pwd, minPwd, maxPwd), ctx.getString(R.string.pattern_pwd_error, minPwd, maxPwd)),
            RegexValidator(inputPwdCheck, ctx.getString(R.string.pattern_pwd, minPwd, maxPwd), ctx.getString(R.string.pattern_pwd_error, minPwd, maxPwd))
        )

        /*view.inputUserName.apply {
            onFocusChangeListener =
                    RegexValidator(this, ctx.getString(R.string.pattern_username, minUserName, maxUserName), ctx.getString(R.string.pattern_username_error))

        }
        view.inputEmail.apply {
            onFocusChangeListener =
                    RegexValidator(this, ctx.getString(R.string.pattern_email), ctx.getString(R.string.pattern_email_error))

        }
        view.inputPwd.apply {
            onFocusChangeListener =
                    RegexValidator(this, ctx.getString(R.string.pattern_pwd, minPwd, maxPwd), ctx.getString(R.string.pattern_pwd_error))
        }
        view.inputPwdCheck.apply {
            onFocusChangeListener =
                    RegexValidator(this, ctx.getString(R.string.pattern_pwd, minPwd, maxPwd), ctx.getString(R.string.pattern_pwd_error))
        }*/

        btnRegister.setOnClickListener { validateAndReturn() }
        btnCancel.setOnClickListener { dismiss() }
    }

    override fun onStart() {
        super.onStart()
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    private fun validateAndReturn() {
        var validAll = true
        for (v in validators) {
            val valid = v.validate()
            validAll = validAll && valid
        }

        if (!validAll) {
            showValidationError("Please fix validation error")
        } else if (inputPwd.text.toString() != inputPwdCheck.text.toString()) {
            showValidationError("Passwords do not match")
        } else {
            val user = UserInfo("", inputUserName.text.toString(), inputEmail.text.toString())
            dismiss()
            submitListener?.invoke(user, inputPwd.text.toString())
        }
    }

    private fun showValidationError(text: String) {
        Snackbar.make(inputUserName, text, Snackbar.LENGTH_LONG).colorError().show()
    }

    private class RegexValidator(private val editText: EditText, regexString: String, private val errorMessage: String) /*: View.OnFocusChangeListener,
        TextWatcher*/ {
        private val regex = Regex(regexString)
/*
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            if (!hasFocus) {
                editText.error = if (!regex.matches(editText.text)) errorMessage else null
                Log.i(LOGCAT, "FOCUS LOST")
            }
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}*/

        fun validate(): Boolean {
            editText.error = if (!regex.matches(editText.text)) errorMessage else null
            return editText.error == null
        }

        /*      override fun afterTextChanged(s: Editable?) {}

              override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}*/
    }
}