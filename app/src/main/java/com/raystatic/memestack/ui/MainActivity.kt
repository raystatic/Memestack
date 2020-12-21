package com.raystatic.memestack.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.raystatic.memestack.databinding.ActivityMainBinding
import com.raystatic.memestack.other.Status
import com.raystatic.memestack.ui.viewmodels.MainViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val vm by viewModels<MainViewModel>()

    @Inject
    lateinit var glide: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnNext.setOnClickListener {
            vm.getMemes()
        }

        binding.imgShareWhatsapp.setOnClickListener {
            shareMeme()
        }

        setObservers()

    }

    private fun shareMeme() {

        if (currentUrl.isEmpty()) Toast.makeText(this, "Please load a meme first", Toast.LENGTH_SHORT).show()
        else{
            Picasso.get().load(currentUrl).into(object : com.squareup.picasso.Target{
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {

                }

                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    try {

                       // val uri: Uri = Uri.parse(MediaStore.Images.Media.insertImage(contentResolver, BitmapFactory.decodeFile(fileUri), null, null))
                        // use intent to share image
                        // use intent to share image
                        val share = Intent(Intent.ACTION_SEND)
                        share.type = "image/*"
                        share.setPackage("com.whatsapp")
                        share.putExtra(Intent.EXTRA_STREAM, getBitmapFromView(bitmap))
                        startActivity(share)

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }catch (e: ActivityNotFoundException){
                        Toast.makeText(this@MainActivity, "Whatsapp not found", Toast.LENGTH_SHORT).show()
                        try {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse("market://details?id=com.whatsapp")
                            startActivity(intent)
                        } catch (e: java.lang.Exception) {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp")
                            intent.flags = FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                    }
                }
            })
        }

    }

    fun getBitmapFromView(bmp: Bitmap?): Uri? {
        var bmpUri: Uri? = null
        try {
            val file = File(this.externalCacheDir, System.currentTimeMillis().toString() + ".jpg")
            val out = FileOutputStream(file)
            bmp?.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.close()
            bmpUri = Uri.fromFile(file)

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmpUri
    }

    private fun setObservers() {
        vm.getMemes.observe(this, Observer {
            when(it.status){
                Status.SUCCESS -> {
                    it.data?.let {res->
                        binding.apply {

                            currentUrl = res.url
                            glide.load(res.url)
                                .listener(object : RequestListener<Drawable>{
                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        loader.isVisible = false
                                        Toast.makeText(this@MainActivity, "Please load next meme", Toast.LENGTH_SHORT).show()
                                        return false
                                    }

                                    override fun onResourceReady(
                                        resource: Drawable?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        dataSource: DataSource?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        currentMeme = resource
                                        loader.isVisible = false
                                        return false
                                    }
                                })
                                .into(imgMeme)
                        }
                    }!!
                }

                Status.LOADING -> {
                    loader.isVisible = true
                }

                Status.ERROR -> {
                    Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
                    loader.isVisible = false
                }
            }
        })
    }

    companion object{
        private var currentMeme:Drawable? = null
        private var currentUrl:String  =""
    }

}