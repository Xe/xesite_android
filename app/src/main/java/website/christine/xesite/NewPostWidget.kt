package website.christine.xesite

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack

/**
 * Implementation of App Widget functionality.
 */
class NewPostWidget : AppWidgetProvider() {
    private lateinit var requestQueue: RequestQueue

    override fun onUpdate(
        ctx: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val url = "https://christine.website/.within/website.within.xesite/new_post"

        val jor: GsonGetRequest<NewPost> = GsonGetRequest(
            url,
            NewPost::class.java,
            null,
            Response.Listener<NewPost> { response ->
                Log.println(Log.INFO, "new_post", response.toString())
                // There may be multiple widgets active, so update all of them
                for (appWidgetId in appWidgetIds) {
                    this.updateAppWidget(ctx, appWidgetManager, appWidgetId, response)
                }
            }, Response.ErrorListener { error ->
                // There may be multiple widgets active, so update all of them
                for (appWidgetId in appWidgetIds) {
                    this.updateAppWidget(
                        ctx,
                        appWidgetManager,
                        appWidgetId,
                        NewPost("Error", error.toString(), "")
                    )
                }
            })

        if (!this::requestQueue.isInitialized) {
            this.makeQueue(ctx)
        }
        this.requestQueue.add(jor)
    }

    private fun makeQueue(ctx: Context) {
        // Instantiate the cache
        val cache = DiskBasedCache(ctx.cacheDir, 1024 * 1024) // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        val network = BasicNetwork(HurlStack())

        this.requestQueue = RequestQueue(cache, network).apply {
            start()
        }
    }

    override fun onEnabled(ctx: Context) {
        this.makeQueue(ctx)
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        body: NewPost
    ) {
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.new_post_widget)

        views.setTextViewText(
            R.id.article_title,
            body.title
        )
        views.setTextViewText(
            R.id.article_preview,
            body.summary
        )

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}