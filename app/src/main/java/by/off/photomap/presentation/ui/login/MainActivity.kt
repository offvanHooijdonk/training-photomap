package by.off.photomap.presentation.ui.login

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import by.off.photomap.R
import by.off.photomap.core.ui.colorRes
import by.off.photomap.core.ui.initializeCategoriesViewProps
import by.off.photomap.core.ui.title
import by.off.photomap.storage.parse.impl.CategoryServiceImpl
import com.parse.ParseUser
import kotlinx.android.synthetic.main.act_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var ctx: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_main)
        ctx = this

        btnLogOut.setOnClickListener {
            ProgressDialog.show(this, "", "", true, false) // TODO refactor
            ParseUser.logOutInBackground { e ->
                // TODO handle
                navigateToLogin()
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            val categories = withContext(Dispatchers.IO) {
                CategoryServiceImpl().list()
            }
            initializeCategoriesViewProps(ctx, categories.map { it.label })
            for (cat in categories) {
                blockCategories.addView(TextView(ctx).apply {
                    text = cat.title
                    setTextColor(ctx.resources.getColor(cat.colorRes))
                })
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}