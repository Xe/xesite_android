package website.christine.xesite

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.LinearLayout
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class NewPostWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        val views = RemoteViews(context.packageName, R.layout.new_post_widget)
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.new_post_widget)
    views.setTextViewText(R.id.article_title, "My Thoughts About Using Android Again as an iPhone User")
    views.setTextViewText(R.id.appwidget_text2, "I used to be a hardcore Android user. It was my second major kind of smartphone (the first was Windows Mobile 6.1 on a T-Mobile Dash) and it left me hooked to the concept of smartphones and connected tech in general.")

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}