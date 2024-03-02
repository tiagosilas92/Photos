package com.photos.volley

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.photos.R
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.URL

class MainActivity() : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var photoImageView: ImageView
    private lateinit var thumbnailImageView: ImageView
    private lateinit var loadButton: Button

    constructor(parcel: Parcel) : this() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        titleTextView = findViewById(R.id.titleTextView)
        photoImageView = findViewById(R.id.photoImageView)
        thumbnailImageView = findViewById(R.id.thumbnailImageView)
        loadButton = findViewById(R.id.loadButton)

        loadButton.setOnClickListener {
            loadPhotos()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadPhotos() {
        val url = "https://jsonplaceholder.typicode.com/photos"

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                // Handle the JSON array response
                parsePhotos(response)
            },
            { error ->
                titleTextView.text = "Error: ${error.message}"
            }
        )

        // Add the request to the RequestQueue.
        Volley.newRequestQueue(this).add(jsonArrayRequest)
    }

    private fun parsePhotos(response: JSONArray) {
        val titles = ArrayList<String>()
        val photoUrls = ArrayList<String>()
        val thumbnailUrls = ArrayList<String>()

        for (i in 0 until response.length()) {
            val photoObject: JSONObject = response.getJSONObject(i)
            val title: String = photoObject.getString("title")
            val photoUrl: String = photoObject.getString("url")
            val thumbnailUrl: String = photoObject.getString("thumbnailUrl")

            titles.add(title)
            photoUrls.add(photoUrl)
            thumbnailUrls.add(thumbnailUrl)
        }

        "Select a photo".also { titleTextView.text = it }

        loadPhoto(photoUrls[0], photoImageView)
        loadPhoto(thumbnailUrls[0], thumbnailImageView)

        titleTextView.setOnClickListener {
            val selectedPhotoIndex = (0 until titles.size).random()
            titleTextView.text = titles[selectedPhotoIndex]
            loadPhoto(photoUrls[selectedPhotoIndex], photoImageView)
            loadPhoto(thumbnailUrls[selectedPhotoIndex], thumbnailImageView)
        }
    }

    private fun loadPhoto(url: String, imageView: ImageView) {
        Thread {
            try {
                val inputStream: InputStream = URL(url).openStream()
                val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

                runOnUiThread {
                    imageView.setImageBitmap(bitmap)
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    companion object CREATOR : Parcelable.Creator<MainActivity> {
        override fun createFromParcel(parcel: Parcel): MainActivity {
            return MainActivity(parcel)
        }

        override fun newArray(size: Int): Array<MainActivity?> {
            return arrayOfNulls(size)
        }
    }
}
