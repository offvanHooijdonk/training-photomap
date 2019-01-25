package by.off.photomap.storage.parse.impl

import android.content.Context
import com.parse.Parse

class ParseHelper {
    companion object {
        fun initParse(ctx: Context) {
            Parse.initialize(
                Parse.Configuration.Builder(ctx)
                    .applicationId(ctx.getString(R.string.parse_app_id))
                    .clientKey(ctx.getString(R.string.parse_client_key))
                    .server(ctx.getString(R.string.parse_server_url))
                    .build()
            )
        }
    }

}
