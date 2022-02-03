package com.codepath.apps.restclienttemplate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.apps.restclienttemplate.models.TweetsAdapter
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException

class TimelineActivity : AppCompatActivity() {

    lateinit var swipeContainer: SwipeRefreshLayout

    lateinit var client: TwitterClient

    lateinit var rvTweets: RecyclerView

    lateinit var adapter: TweetsAdapter

    val tweets = ArrayList<Tweet>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_timeline)
        swipeContainer = findViewById(R.id.swipeContainer)

        swipeContainer.setOnRefreshListener {
            Log.i(TAG, "Refreshing Timeline")
            populateHomeTimeline()

        }

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light)



        client = TwitterApplication.getRestClient(this)

        rvTweets = findViewById(R.id.rvTweets)
        adapter = TweetsAdapter(tweets)

        rvTweets.layoutManager = LinearLayoutManager(this)
        rvTweets.adapter = adapter


        populateHomeTimeline()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        return true
    }

    //Handles clicks on menu items
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.compose)
        {
            // Navigate to compose screen
            val intent = Intent(this, ComposeActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)

        }
        return super.onOptionsItemSelected(item)
    }

    //Method is called when we get back from ComposeActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == RESULT_OK && requestCode ==  REQUEST_CODE)
        {
            //Get data from our intent
            val tweet = data?.getParcelableExtra("tweet") as Tweet

            //Update timeline
            //Modifying data source of tweets
            tweets.add(0,tweet)
            //Update adapter
            adapter.notifyItemInserted(0)
            rvTweets.smoothScrollToPosition(0)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun populateHomeTimeline()
    {
        client.getHomeTimeline(object : JsonHttpResponseHandler()
        {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                Log.i(TAG, "onSuccess!")

                val jsonArray = json.jsonArray

                try {
                    adapter.clear()
                    val listOfNewTweetsRetrieved = Tweet.fromJsonArray(jsonArray)
                    tweets.addAll(listOfNewTweetsRetrieved)
                    adapter.notifyDataSetChanged()
                    swipeContainer.isRefreshing = false
                }catch(e: JSONException){
                    Log.e(TAG, "Json Exception $e")
                }

            }


            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.i(TAG, "onFailure! $response")
            }


        })
    }
    companion object{
        val TAG = "TimelineActivity"
        val REQUEST_CODE = 69
    }
}