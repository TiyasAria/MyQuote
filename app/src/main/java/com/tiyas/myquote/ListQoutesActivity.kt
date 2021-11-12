package com.tiyas.myquote

import QuoteAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.tiyas.myquote.databinding.ActivityListQoutesBinding
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class ListQoutesActivity : AppCompatActivity() {

    companion object{
        private val TAG = ListQoutesActivity::class.java.simpleName
    }

    private  lateinit var listQoutesBinding: ActivityListQoutesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listQoutesBinding = ActivityListQoutesBinding.inflate(layoutInflater)
        setContentView(listQoutesBinding.root)

        supportActionBar?.title = "List of qutoes"

        val layoutManager = LinearLayoutManager(this)
        listQoutesBinding.listQuotes.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        listQoutesBinding.listQuotes.addItemDecoration(itemDecoration)

        getListQuotes()

    }

    private fun getListQuotes() {
        listQoutesBinding.progressBar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        val url = "https://quote-api.dicoding.dev/list"
        client.get(url, object : AsyncHttpResponseHandler(){
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                listQoutesBinding.progressBar.visibility = View.INVISIBLE

                val listQuote = ArrayList<String>()
                val result = String(responseBody!!)
                Log.d(TAG, result)
                try {
                    val jsonArray = JSONArray(result)

                    for (i in 0 until jsonArray.length()){
                        val jsonObject = jsonArray.getJSONObject(i)
                        val quote = jsonObject.getString("en")
                        val author = jsonObject.getString("author")
                        listQuote.add("\n$quote\n - $author\n")
                    }

                    val adapter = QuoteAdapter(listQuote)
                    listQoutesBinding.listQuotes.adapter = adapter
                } catch (e: Exception){
                    Toast.makeText(this@ListQoutesActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                listQoutesBinding.progressBar.visibility = View.INVISIBLE
                val errorMessage = when(statusCode){
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else  -> "$statusCode : ${error!!.message}"
                }
                Toast.makeText(this@ListQoutesActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }

        })
    }
}