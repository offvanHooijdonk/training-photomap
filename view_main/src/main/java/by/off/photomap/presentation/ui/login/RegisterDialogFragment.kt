package by.off.photomap.presentation.ui.login

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import by.off.photomap.core.ui.colorError
import by.off.photomap.model.UserInfo
import by.off.photomap.presentation.ui.R
import kotlinx.android.synthetic.main.dialog_register.*

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

        // @formatter:off
        validators = mutableListOf(
            RegexValidator(inputUserName, ctx.getString(R.string.pattern_username, minUserName, maxUserName), ctx.getString(R.string.pattern_username_error, minUserName, maxUserName), tilUserName),
            RegexValidator(inputEmail, ctx.getString(R.string.pattern_email), ctx.getString(R.string.pattern_email_error), tilEmail),
            RegexValidator(inputPwd, ctx.getString(R.string.pattern_pwd, minPwd, maxPwd), ctx.getString(R.string.pattern_pwd_error, minPwd, maxPwd), tilPwd),
            RegexValidator(inputPwdCheck, ctx.getString(R.string.pattern_pwd, minPwd, maxPwd), ctx.getString(R.string.pattern_pwd_error, minPwd, maxPwd), tilPwdCheck)
        )
        // @formatter:on

        btnRegister.setOnClickListener { validateAndReturn() }
        btnCancel.setOnClickListener {
            if (shouldConfirm())
                AlertDialog.Builder(ctx)
                    .setCancelable(false)
                    .setTitle(R.string.dialog_confirm_title)
                    .setMessage(R.string.dialog_confirm_cancel_reg)
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes) { dialog, _ -> dialog.dismiss(); this@RegisterDialogFragment.dismiss() }
                    .create().show()
            else
                this.dismiss()
        }
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
            showValidationError("Please fix validation errorMessage")
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

    private fun shouldConfirm(): Boolean =
        !inputUserName.text.isEmpty() || !inputEmail.text.isEmpty() || !inputPwd.text.isEmpty() || !inputPwdCheck.text.isEmpty()

    private class RegexValidator(private val editText: EditText, regexString: String, private val errorMessage: String, private val target: TextInputLayout) {
        private val regex = Regex(regexString)

        fun validate(): Boolean {
            target.error = if (!regex.matches(editText.text)) errorMessage else null
            return target.error == null
        }
    }
}