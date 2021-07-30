package com.example.bakunov_test_task

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL

class NewsActivity : AppCompatActivity() {

    private val inputStream: InputStream? = null
    private val address = intent.getStringExtra("address")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        // забираем адрес из переданного интента

//        Toast.makeText(this, address, Toast.LENGTH_LONG).show()
    }

    private fun getInputStream(url: URL): InputStream?{
        return try {
            url.openConnection().getInputStream()
        } catch (e: IOException){
            null
        }
    }

    private suspend fun downloadParsingRSS(){
        try {
            val url = URL(address)
            val factory: XmlPullParserFactory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = false
            val xpp: XmlPullParser = factory.newPullParser()
            xpp.setInput(getInputStream(url), "UTF_8")
            var insideItem: Boolean = false
            val eventType: Int = xpp.eventType

            while(eventType != XmlPullParser.END_DOCUMENT){
                if (eventType == XmlPullParser.START_TAG){
                    if (xpp.name.equals("item")){
                        insideItem = true
                    }
                }
            }
        } catch (e: MalformedURLException){

        }
    }

    private fun downloadNews(){
        CoroutineScope(Dispatchers.Main).launch {

        }
    }
}