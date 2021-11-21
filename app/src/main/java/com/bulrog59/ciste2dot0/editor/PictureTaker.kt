package com.bulrog59.ciste2dot0.editor

import android.graphics.Bitmap
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.ResourceManager
import com.bulrog59.ciste2dot0.camera.util.CameraManager
import com.bulrog59.ciste2dot0.camera.util.ConvertPicture
import org.opencv.android.Utils
import org.opencv.core.Mat

class PictureTaker(
    val activity: AppCompatActivity,
    private val fileName:String,
    private val cameraManager: CameraManager,
    private val callBack: () -> Unit

) : ImageAnalysis.Analyzer {
    private var capture = false
    private val resourceManager=ResourceManager(activity)
    init {
        val button = activity.findViewById<Button>(R.id.camera_capture_button)
        button.visibility=View.VISIBLE
        button.setOnClickListener { takePhoto() }
    }


    fun takePhoto() {
        capture = true
    }

    private fun createBitmapfromMat(snap: Mat): Bitmap? {
        val bp = Bitmap.createBitmap(snap.cols(), snap.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(snap, bp)
        return bp
    }

    override fun analyze(imageProxy: ImageProxy) {

        val img = ConvertPicture.getPicture(imageProxy)
        if (capture) {
            resourceManager.getOutputStreamForFile("$fileName.jpg").use {
                createBitmapfromMat(img)?.compress(Bitmap.CompressFormat.JPEG, 95, it)
                    //TODO: replace with string resource
                    ?:Toast.makeText(activity,"Failed to save the bitmap, cannot create a new file", Toast.LENGTH_LONG).show()
                cameraManager.stopCamera()
                callBack()}

        }

        imageProxy.close()
    }

}