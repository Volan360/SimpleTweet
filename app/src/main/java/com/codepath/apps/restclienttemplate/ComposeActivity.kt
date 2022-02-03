package com.codepath.apps.restclienttemplate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {
    lateinit var etCompose: EditText
    lateinit var btnTweet: Button
    lateinit var client: TwitterClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)

        client = TwitterApplication.getRestClient(this)

        //Handle clicking tweet
        btnTweet.setOnClickListener{

            //get content of edittext
            val tweetContent = etCompose.text.toString()

            //1. Check tweet for emptyness
            if(tweetContent.isEmpty())
            {
                Toast.makeText(this, "No empty Tweets!", Toast.LENGTH_SHORT).show()
            }
            //2. Make sure tweet is under character count

            else
            {
                if (tweetContent.length > 280)
                {
                    Toast.makeText(this, "Tweet can't be more than 280 characters!", Toast.LENGTH_SHORT).show()
                }

                else
                {
                    client.publishTweet(tweetContent, object : JsonHttpResponseHandler(){

                        override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                            Log.i(TAG, "Successfully published tweet!")

                            val tweet = Tweet.fromJson(json.jsonObject)

                            val intent = Intent()
                            intent.putExtra("tweet", tweet)
                            setResult(RESULT_OK, intent)
                            finish()
                        }

                        override fun onFailure(
                            statusCode: Int,
                            headers: Headers?,
                            response: String?,
                            throwable: Throwable?
                        ) {
                            Log.e(TAG, "Failed to publish tweet", throwable)
                        }


                    })

                }
            }

        }

    }
    companion object{
        val TAG = "ComposeActivity"
    }
}