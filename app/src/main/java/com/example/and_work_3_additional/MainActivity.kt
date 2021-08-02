package com.example.and_work_3_additional

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.squareup.picasso.Picasso
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var inputLink: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.image_loaded)
        inputLink = findViewById(R.id.input_link)
        if (savedInstanceState != null) {
            savedInstanceState.getString(IMAGE_LINK)?.let {
                if (it != "")
                    Thread(ImageLoader(it)).start()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(IMAGE_LINK, inputLink.text.toString())
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
        val imageLoader = ImageLoader(imageLink)
        Thread(imageLoader).start()
    }

    private fun setErrorImageAndToast() {
        imageView.setImageResource(R.drawable.failed_load)
        Toast
            .makeText(this, getString(R.string.error_image_load_failed), Toast.LENGTH_SHORT)
            .show()
    }

    inner class ImageLoader(private val link: String?) : Runnable {
        override fun run() {
            val image: Bitmap? = try {
                val imageURL = URL(link)
                BitmapFactory.decodeStream(imageURL.openConnection().getInputStream())
            } catch (e: Exception) {
                null
            }
            imageView.post {
                if (image != null) {
                    imageView.setImageBitmap(image)
                } else {
                    setErrorImageAndToast()
                }
            }
        }
    }

    companion object {
        const val IMAGE_LINK = "image_link"
    }
}