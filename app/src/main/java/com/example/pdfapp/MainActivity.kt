package com.example.pdfapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.pdfapp.databinding.ActivityMainBinding
import com.github.barteksc.pdfviewer.PDFView

const val TAG = "MAINACTIVITY"

class MainActivity : AppCompatActivity() {
    lateinit var pdfView: PDFView
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pdfView = binding.pdfView
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

    private fun showPdfFromUri(uri: Uri?, pdfView: PDFView) {
        pdfView.fromUri(uri).defaultPage(0).enableSwipe(true).swipeHorizontal(true).load()

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
                    showPdfFromUri(data.data, pdfView)
                }
            }
        }
    }

}
