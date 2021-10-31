package com.bulrog59.ciste2dot0.scenes.detector

import android.content.pm.ActivityInfo
import android.view.View
import android.widget.Button
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.bulrog59.ciste2dot0.CisteActivity
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.scenes.Scene
import java.lang.IllegalStateException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class DetectorScene(private val detectorOption: DetectorOption, private val cisteActivity: CisteActivity) : Scene {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var picDetector: PictureDetector


    private fun startCamera() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(cisteActivity)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(cisteActivity.findViewById<PreviewView>(R.id.viewFinder).surfaceProvider)
                }

            picDetector = PictureDetector(detectorOption, cisteActivity, this)
            // Set up the listener for take photo button
            val button = cisteActivity.findViewById<Button>(R.id.camera_capture_button)
            if (detectorOption.allow_capture) {
                button.setOnClickListener { picDetector.takePhoto() }

            } else {
                button.visibility = View.INVISIBLE
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .apply {
                    setAnalyzer(
                        cameraExecutor,
                        picDetector
                    )
                }


            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()

            cameraProvider.bindToLifecycle(
                cisteActivity, cameraSelector, preview, imageAnalyzer
            )

        }, ContextCompat.getMainExecutor(cisteActivity))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun setup() {
        cisteActivity.setContentView(R.layout.scene_detector)
        cisteActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        startCamera()

    }

    fun stopAndSetScene(sceneID: Int) {
        cameraExecutor.shutdown()
        if (cameraExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
            println("camera is shutdown")
        } else {
            throw IllegalStateException("cannot shutdown the camera executor, please inform the developer")
        }
        cisteActivity.setScene(sceneID)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun shutdown() {
        cameraExecutor.shutdown()
    }


}