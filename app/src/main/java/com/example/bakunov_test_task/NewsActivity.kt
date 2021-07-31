package com.example.bakunov_test_task

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.widget.FrameLayout
import android.widget.Toast
import androidx.constraintlayout.solver.widgets.ConstraintWidget.GONE
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bakunov_test_task.adapters.ItemsAdapter
import com.example.bakunov_test_task.data.ItemRss
import kotlinx.coroutines.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.net.MalformedURLException
import java.net.URL
import androidx.lifecycle.lifecycleScope

class NewsActivity : AppCompatActivity() {

    private lateinit var itemRv: RecyclerView
    private lateinit var itemsRssAdapter: ItemsAdapter
    private lateinit var progressbarLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        // забираем адрес из переданного интента
        val address: String? = intent.getStringExtra("address")
        initView()
        downloadNews(address)
    }

    // инициализация view
    private fun initView() {
        progressbarLayout = findViewById(R.id.progressbarLayout)
        itemRv = findViewById(R.id.recyclerView)
    }

    // показать progressBar
    private fun showProgressBar(){
        progressbarLayout.visibility = View.VISIBLE
    }

    // скрыть progressBar
    private fun hideProgressBar(){
        progressbarLayout.visibility = View.INVISIBLE
    }

    // метод для получения inputStream
    private fun getInputStream(url: URL): InputStream?{
        return try {
            url.openConnection().getInputStream()
        } catch (e: IOException){
            null
        }
    }

    // метод парсинга XML
    private suspend fun parsingRSS(address: String?): MutableList<ItemRss>?{
        try {
            val itemRssList = mutableListOf<ItemRss>()
            val url = URL(address)
            val factory: XmlPullParserFactory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = false
            val xpp: XmlPullParser = factory.newPullParser()
            xpp.setInput(getInputStream(url), "UTF_8")
            var insideItem: Boolean = false
            var eventType: Int = xpp.eventType
            var itemRss: ItemRss? = null

            while(eventType != XmlPullParser.END_DOCUMENT){
                if (eventType == XmlPullParser.START_TAG){
                    if (xpp.name.equals("item", true)){
                        insideItem = true
                        itemRss = ItemRss()
                    }
                    else if (xpp.name.equals("title", true) && insideItem){
                        itemRss?.title = xpp.nextText()
                    }
                    else if (xpp.name.equals("description", true) && insideItem){
                        itemRss?.description = xpp.nextText()
                    }
                    else if (xpp.name.equals("category", true) && insideItem){
                        itemRss?.category = xpp.nextText()
                    }
                    else if (xpp.name.equals("enclosure", true) && insideItem){
                        itemRss?.imgUrl = xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "url")
                    }
                }
                else if (eventType == XmlPullParser.END_TAG && xpp.name.equals("item", true)){
                    insideItem = false

                    itemRssList.add(itemRss!!)
                }

                eventType = xpp.next()
            }

            return itemRssList
        } catch (e: MalformedURLException){
            Log.d("exceptionUrl", e.toString())
        } catch (e: XmlPullParserException){
            Log.d("exceptionUrl", e.toString())
        } catch (e: IOException){
            Log.d("exceptionUrl", e.toString())
        }
        return null
    }

    // корутина для загрузка и сета данных в recyclerView
    private fun downloadNews(address: String?){
        CoroutineScope(Dispatchers.Main).launch {
            showProgressBar()
            val list: MutableList<ItemRss>? = withContext(Dispatchers.IO) { parsingRSS(address) }
            if (list != null){
                itemsRssAdapter = ItemsAdapter(list){
                    Toast.makeText(this@NewsActivity, it.title, Toast.LENGTH_LONG).show()
                }
                itemRv.apply {
                    adapter = itemsRssAdapter
                    layoutManager = LinearLayoutManager(this@NewsActivity)
                }
                hideProgressBar()
            }
            else{
                Toast.makeText(this@NewsActivity, "Ошибка!", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

}