package by.off.photomap.presentation.ui.login

import android.app.ProgressDialog
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import by.off.photomap.R
import com.parse.ParseUser
import kotlinx.android.synthetic.main.act_main.*
import android.content.Intent
import android.support.v7.app.AlertDialog


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.act_main)

        btnLogOut.setOnClickListener {
            ProgressDialog.show(this, "", "", true, false) // TODO refactor
            ParseUser.logOutInBackground { e ->
                // TODO handle
                navigateToLogin()
            }
        }
    }

    fun navigateToLogin() {
        val intent = Intent(this, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}