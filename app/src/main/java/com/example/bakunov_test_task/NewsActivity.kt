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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bakunov_test_task.adapters.ItemsAdapter
import com.example.bakunov_test_task.data.ItemRss
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL

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
        initRV()
        downloadNews(address)
    }

    private fun initView() {
        progressbarLayout = findViewById(R.id.progressbarLayout)
    }

    private fun showProgressBar(){
        progressbarLayout.visibility = View.VISIBLE
    }

    private fun hideProgressBar(){
        progressbarLayout.visibility = View.INVISIBLE
    }

    private fun initRV() {
        itemRv = findViewById(R.id.recyclerView)
    }

    private fun getInputStream(url: URL): InputStream?{
        return try {
            url.openConnection().getInputStream()
        } catch (e: IOException){
            null
        }
    }

    private suspend fun parsingRSS(address: String?): MutableList<ItemRss>?{
        Log.d("parsing", "start")
        try {
            Log.d("parsing", "try")
            val itemRssList = mutableListOf<ItemRss>()
            val url = URL(address)
            Log.d("parsing", "url: ${url.toString()}")
            val factory: XmlPullParserFactory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = false
            val xpp: XmlPullParser = factory.newPullParser()
            xpp.setInput(getInputStream(url), null)
            var insideItem: Boolean = false
            var eventType: Int = xpp.eventType
            var itemRss: ItemRss? = null

            while(eventType != XmlPullParser.END_DOCUMENT){
                //Log.d("parsing", "while: ${eventType.toString()}")
                if (eventType == XmlPullParser.START_TAG){
                    //Log.d("parsing", "START_TAG")
                    if (xpp.name.equals("item", true)){
                        insideItem = true
                        //Log.d("parsing", "insideItem true")
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
                        itemRss?.imgUrl = xpp.getAttributeValue(1)
                    }
                }
                else if (eventType == XmlPullParser.END_TAG && xpp.name.equals("item", true)){
                    insideItem = false

                    itemRssList.add(itemRss!!)
                }

                eventType = xpp.next()
            }
            Log.d("parsing", itemRssList.size.toString())
            return itemRssList
        } catch (e: MalformedURLException){

        }
        return null
    }

    private fun downloadNews(address: String?){
        CoroutineScope(Dispatchers.Main).launch {
            showProgressBar()
            val list: MutableList<ItemRss>? = withContext(Dispatchers.IO) { parsingRSS(address) }
            itemsRssAdapter = ItemsAdapter(list){
                Toast.makeText(this@NewsActivity, it.title, Toast.LENGTH_LONG).show()
            }
            itemRv.apply {
                adapter = itemsRssAdapter
                layoutManager = LinearLayoutManager(this@NewsActivity)
            }
            hideProgressBar()
//            list?.forEach {
//                Log.d("foreach", "imgUrl: ${it.imgUrl}")
//            }


        }
    }
}