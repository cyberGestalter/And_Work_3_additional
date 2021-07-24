package com.example.and_work_3_additional

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.squareup.picasso.Picasso
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val IMAGE = "image"
    private lateinit var imageView: ImageView
    private lateinit var inputLink: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.image_loaded)
        if (savedInstanceState != null) {
            val image: Bitmap? = savedInstanceState.getParcelable(IMAGE)
            imageView.setImageBitmap(image)
        }
        inputLink = findViewById(R.id.input_link)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val bitmap = try {
            (imageView.drawable as BitmapDrawable).bitmap
        } catch (e: ClassCastException) {
            (imageView.drawable as VectorDrawable).toBitmap()
        }
        outState.putParcelable(IMAGE, bitmap)
    }

    fun loadImageWithPicasso(view: View) {
        imageView.setImageResource(R.drawable.before_load)
        val imageLink = inputLink.text.toString()
        if (imageLink.isEmpty()) {
            setErrorImageAndToast()
            return
        }

        val errorListener = Picasso.Listener { _, _, _ -> setErrorImageAndToast() }

        Picasso.Builder(this)
            .listener(errorListener)
            .build()
            .load(imageLink)
            .error(R.drawable.failed_load)
            .into(imageView)
    }

    fun loadImageWithGlide(view: View) {
        imageView.setImageResource(R.drawable.before_load)
        val imageLink = inputLink.text.toString()
        if (imageLink.isEmpty()) {
            setErrorImageAndToast()
            return
        }

        val errorListener = object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?, model: Any?, target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                setErrorImageAndToast()
                return false
            }

            override fun onResourceReady(
                resource: Drawable?, model: Any?, target: Target<Drawable>?,
                dataSource: DataSource?, isFirstResource: Boolean
            ): Boolean {
                return false
            }
        }

        Glide.with(this)
            .load(imageLink)
            .error(R.drawable.failed_load)
            .listener(errorListener)
            .into(imageView)
    }

    fun loadImageWithAndroid(view: View) {
        imageView.setImageResource(R.drawable.before_load)
        val imageLink = inputLink.text.toString()
        ImageLoader().execute(imageLink)
    }

    private fun setErrorImageAndToast() {
        imageView.setImageResource(R.drawable.failed_load)
        Toast
            .makeText(this, getString(R.string.error_image_load_failed), Toast.LENGTH_SHORT)
            .show()
    }

    inner class ImageLoader : AsyncTask<String, Any, Any>() {
        override fun doInBackground(vararg params: String?): Any? {
            return try {
                val link = params[0]
                val imageURL = URL(link)
                BitmapFactory.decodeStream(imageURL.openConnection().getInputStream())
            } catch (e: Exception) {
                null
            }
        }

        override fun onPostExecute(result: Any?) {
            if (result != null) {
                imageView.setImageBitmap(result as Bitmap)
            } else {
                setErrorImageAndToast()
            }
        }

    }
}