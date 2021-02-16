package com.example.pdfapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toFile
import androidx.viewpager2.widget.ViewPager2
import app.num.numandroidpagecurleffect.PageCurlView
import com.example.pdfapp.databinding.ActivityMainBinding
import com.github.barteksc.pdfviewer.PDFView
import com.wajahatkarim3.easyflipviewpager.BookFlipPageTransformer2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

const val TAG = "MAINACTIVITY"

class MainActivity : AppCompatActivity() {
    lateinit var viewPager2: ViewPager2
    lateinit var binding: ActivityMainBinding
    lateinit var viewPagerAdapter: MyViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewPager2 = binding.viewPager
        if (permissionGranted()) {
            showPdfFromStorage()
        }

    }

    private fun showPdfFromStorage() {
        val browseStorage = Intent(Intent.ACTION_GET_CONTENT)
        browseStorage.type = "application/pdf"
//        it only opens those pdf which can be open by openFileDescriptor(uri:String)
        browseStorage.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(Intent.createChooser(browseStorage, "Select Pdf"), 123)
    }

    private fun permissionGranted(): Boolean {
//        check for marshmallow and above
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE),
                    3
                )
                return false
            }
        } else {
            return true
        }
    }

    private lateinit var list: List<Bitmap>
    private fun showPdfFromUri(uri: Uri?) {
        if (uri != null) {
            Toast.makeText(this, "${uri.path}", Toast.LENGTH_SHORT).show()
            GlobalScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.IO) {
                    list = convertPdfToBitmap(uri)
                }
                Log.e(TAG,"${list.size}")
                viewPagerAdapter = MyViewPagerAdapter(list)
                val bookFlipPageTransformer2 = BookFlipPageTransformer2()
//                bookFlipPageTransformer2.setEnableScale(true)
////                bookFlipPageTransformer2.scaleAmountPercent = 10f
                viewPager2.adapter=viewPagerAdapter
                viewPager2.setPageTransformer(bookFlipPageTransformer2)
            }
        }
    }

    //
    private suspend fun convertPdfToBitmap(uri: Uri?): ArrayList<Bitmap> {
        val bitmapList = arrayListOf<Bitmap>()
        if (uri != null) {
            val input =
                contentResolver.openFileDescriptor(uri, "r")
            val renderer = PdfRenderer(input!!)
            val pageCount = renderer.pageCount
            var count = 0
            Log.e(TAG, "$pageCount")
            while (count < pageCount) {
                val page = renderer.openPage(count)
                val displayMetrics =resources.displayMetrics
                val width = displayMetrics.widthPixels
                val height=displayMetrics.heightPixels
                val bitmap = Bitmap.createBitmap(
                    width,
                    height,
                    Bitmap.Config.ARGB_8888
                )
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bitmapList.add(bitmap)
                page.close()

                count++
            }
            renderer.close()
        }
        return bitmapList
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            3 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showPdfFromStorage()
                    Toast.makeText(this, "Access Granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            123 -> {
                if (resultCode == RESULT_OK && data != null) {
                    showPdfFromUri(data.data)
                }
            }
        }
    }

}
