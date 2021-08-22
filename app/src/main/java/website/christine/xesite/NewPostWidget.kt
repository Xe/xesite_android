package website.christine.xesite

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import java.util.concurrent.ThreadLocalRandom

/**
 * Implementation of App Widget functionality.
 */
class NewPostWidget : AppWidgetProvider() {
    private lateinit var requestQueue: RequestQueue

    private fun userAgent(ctx: Context): String {
        return BuildConfig.APPLICATION_ID
            .plus("/")
            .plus(BuildConfig.VERSION_NAME)
            .plus("(android; ")
            .plus(BuildConfig.BUILD_TYPE)
            .plus("; +https://christine.website/contact")
    }

    private fun notify(ctx: Context, newPost: NewPost) {
        val notificationIntent = Intent(Intent.ACTION_VIEW, Uri.parse(newPost.link))
        val pendingIntent: PendingIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, 0)

        var builder = NotificationCompat.Builder(ctx, NEW_POST_CHANNEL)
            .setSmallIcon(R.drawable.splash)
            .setContentIntent(pendingIntent)
            .setContentTitle(newPost.title)
            .setContentText(newPost.summary)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        NotificationManagerCompat.from(ctx).apply {
            notify(ThreadLocalRandom.current().nextInt(), builder.build())
        }
    }

    override fun onUpdate(
        ctx: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val url = "https://christine.website/.within/website.within.xesite/new_post"

        val headers: MutableMap<String, String> = mutableMapOf()
        headers.put("User-Agent", this.userAgent(ctx));

        val jor: GsonGetRequest<NewPost> = GsonGetRequest(
            url,
            NewPost::class.java,
            headers,
            Response.Listener<NewPost> { response ->
                val oldURL = loadPref(ctx, "old_url", response.link)
                if (response.link != oldURL) {
                    // make notification?
                    this.notify(ctx, response)
                    savePref(ctx, "old_url", response.link)
                }

                Log.println(Log.INFO, "new_post", response.toString())
                // There may be multiple widgets active, so update all of them
                appWidgetIds.forEach { appWidgetId ->
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

    override fun onReceive(ctx: Context?, intent: Intent?) {
        super.onReceive(ctx, intent)
    }

    private fun updateAppWidget(
        ctx: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        body: NewPost
    ) {
        // Construct the RemoteViews object
        val views = RemoteViews(ctx.packageName, R.layout.new_post_widget)

        views.setTextViewText(
            R.id.article_title,
            body.title
        )

        views.setTextViewText(
            R.id.article_preview,
            body.summary
        )

        val pendingIntent: PendingIntent = Intent(Intent.ACTION_VIEW, Uri.parse(body.link))
            .let { intent ->
                PendingIntent.getActivity(ctx, 0, intent, 0)
            }
        views.setOnClickPendingIntent(R.id.widget_parent, pendingIntent)

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}

private const val PREFS_NAME = "website.christine.xesite.NewPostWidget"
private const val PREF_PREFIX_KEY = "appwidget_"

internal fun savePref(ctx: Context, key: String, value: String) {
    val prefs = ctx.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.putString(PREF_PREFIX_KEY + key, value)
    prefs.apply()
}

internal fun loadPref(ctx: Context, key: String, default: String): String {
    val prefs = ctx.getSharedPreferences(PREFS_NAME, 0)
    val titleValue = prefs.getString(PREF_PREFIX_KEY + key, default)
    return titleValue ?: default
}