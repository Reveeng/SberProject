package com.example.sberproject

import android.content.res.AssetManager
import android.graphics.*
import android.media.Image
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.sberproject.Keys.DIM_IMG_SIZE_X
import com.example.sberproject.Keys.DIM_IMG_SIZE_Y
import com.example.sberproject.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.google.mlkit.vision.objects.defaults.PredefinedCategory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.tensorflow.lite.Interpreter
import java.io.*
import java.net.URI
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


class Result(
    val id: String?,
    val title: String?,
    val confidence: Float?,
    private var location: RectF?
) {
    override fun toString(): String {
        var resultString = ""
        if (id != null) resultString += "[$id] "
        if (title != null) resultString += title + " "
        if (confidence != null) resultString += String.format("(%.1f%%) ", confidence * 100.0f)
        if (location != null) resultString += location!!.toString() + " "
        return resultString.trim { it <= ' ' }
    }
}

object Keys {
    const val MODEL_PATH = "mobilenet_quant_v1_224.tflite"
    const val LABEL_PATH = "labels.txt"
    const val INPUT_SIZE = 224
    const val MAX_RESULTS = 3
    const val DIM_BATCH_SIZE = 1
    const val DIM_PIXEL_SIZE = 3
    const val DIM_IMG_SIZE_X = 224
    const val DIM_IMG_SIZE_Y = 224
}

class MainActivity : AppCompatActivity(), MainActivityCallback {

    private lateinit var binding: ActivityMainBinding
    private var interpreter: Interpreter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_scanner,
                R.id.navigation_articles,
                R.id.navigation_maps,
//                R.id.navigation_account
            )
        )
        navView.setupWithNavController(navController)

        //binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        setSupportActionBar(binding.toolbar)
        binding.smt.setPadding(0, 0, 0, 0)

        binding.accButton.setOnClickListener {
            navController.navigate(R.id.accountFragment)
        }
        binding.setButton.setOnClickListener {
            navController.navigate(R.id.navigation_setting)
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.navView.visibility = View.GONE
        binding.accButton.visibility = View.GONE
        binding.setButton.visibility = View.GONE

        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()  // Also possible: .requireCharging() and .requireDeviceIdle()
            .build()
        FirebaseModelDownloader.getInstance()
            .getModel("Trash-Detector", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND,
                conditions)
            .addOnSuccessListener { model: CustomModel? ->
                // Download complete. Depending on your app, you could enable the ML
                // feature, or switch from the local model to the remote model, etc.

                // The CustomModel object contains the local path of the model file,
                // which you can use to instantiate a TensorFlow Lite interpreter.
                val modelFile = model?.file
                if (modelFile != null) {
                    interpreter = Interpreter(modelFile)
                }
            }

        val a = 224
        val bm = BitmapFactory.decodeResource(resources, R.drawable.botle)
        val bitmap = Bitmap.createScaledBitmap(bm, a, a, true)
        val input = ByteBuffer.allocateDirect(a*a*3*4).order(ByteOrder.nativeOrder())
        for (y in 0 until a) {
            for (x in 0 until a) {
                val px = bitmap.getPixel(x, y)

                // Get channel values from the pixel value.
                val r = Color.red(px)
                val g = Color.green(px)
                val b = Color.blue(px)

                // Normalize channel values to [-1.0, 1.0]. This requirement depends on the model.
                // For example, some models might require values to be normalized to the range
                // [0.0, 1.0] instead.
                val rf = (r - 127) / 255f
                val gf = (g - 127) / 255f
                val bf = (b - 127) / 255f

                input.putFloat(rf)
                input.putFloat(gf)
                input.putFloat(bf)
            }
        }
        val bufferSize = 12 * java.lang.Float.SIZE / java.lang.Byte.SIZE
        val modelOutput = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder())
        interpreter?.run(input, modelOutput)
        modelOutput.rewind()
        val probabilities = modelOutput.asFloatBuffer()
        try {
            val reader = BufferedReader(
                InputStreamReader(assets.open("labels.txt")))
            for (i in 0 until probabilities.capacity()) {
                val label: String = reader.readLine()
                val probability = probabilities.get(i)
                println("$label: $probability")
            }
        } catch (e: IOException) {
            // File not found?
        }
        /*for (i in 0 until probabilities.capacity()) {
            println(probabilities.get(i))
            if (probabilities.get(i) > 0.0)
                Toast.makeText(this, "$i: $probabilities.get(i)", Toast.LENGTH_SHORT).show()
        }*/
        /*try {
            val reader = BufferedReader(
                InputStreamReader(assets.open("temp_labels.txt"))
            )
            for (i in 0 until probabilities.capacity()) {
                val label: String = reader.readLine()
                val probability = probabilities.get(i)
                println("$label: $probability")
                if (probability > 0.0)
                    Toast.makeText(this, "$label: $probability", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            throw e
        }*/


        /*val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()  // Optional
            .build()
        val objectDetector = ObjectDetection.getClient(options)
        val image = InputImage.fromBitmap(BitmapFactory.decodeResource(resources, R.drawable.shoes), 0)
        objectDetector.process(image)
            .addOnSuccessListener { detectedObjects ->
                detectedObjects.forEach { detectedObject ->
                    println("****************************************************************************************************************")
                    detectedObject.labels.forEach {
                        println(it.text)
                    }
                }
            }*/
    }

    override fun setActionBarTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun login() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_scanner,
                R.id.navigation_articles,
                R.id.navigation_maps,
//                R.id.navigation_account
            )
        )
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.navView.visibility = View.VISIBLE
        binding.accButton.visibility = View.VISIBLE
        binding.setButton.visibility = View.VISIBLE
        binding.smt.setPadding(
            0,
            (56 * resources.displayMetrics.density + 0.5f).toInt(),
            0,
            (56 * resources.displayMetrics.density + 0.5f).toInt()
        )

    }

    override fun logout() {
        binding.navView.visibility = View.GONE
        binding.accButton.visibility = View.GONE
        binding.setButton.visibility = View.GONE
        binding.smt.setPadding(0, 0, 0, 0)
    }
}