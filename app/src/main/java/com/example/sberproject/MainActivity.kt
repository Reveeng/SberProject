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

//        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.botle)
//        val conditions = CustomModelDownloadConditions.Builder()
//            .requireWifi()
//            .build()
//        FirebaseModelDownloader.getInstance()
//            .getModel("Trash-Detector", DownloadType.LOCAL_MODEL, conditions)
//            .addOnCompleteListener {
//                val model = it.result
//                val modelFile = model?.file
//                if (modelFile != null) {
//                    interpreter = Interpreter(modelFile)
//                }
//            }
        val bm = BitmapFactory.decodeResource(resources, R.drawable.pepsi)
        val bitmap = Bitmap.createScaledBitmap(bm, 224, 224, true)
        val input = ByteBuffer.allocateDirect(224 * 224 * 3 * 4).order(ByteOrder.nativeOrder())
        for (y in 0 until 224) {
            for (x in 0 until 224) {
                val px = bitmap.getPixel(x, y)
                val r = Color.red(px)
                val g = Color.green(px)
                val b = Color.blue(px)
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
                InputStreamReader(assets.open("labels.txt"))
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
        }

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
        binding.smt.setPadding(0, (56 * resources.displayMetrics.density + 0.5f).toInt(), 0, 0)
    }

    override fun logout() {
        binding.navView.visibility = View.GONE
        binding.accButton.visibility = View.GONE
        binding.setButton.visibility = View.GONE
        binding.smt.setPadding(0, 0, 0, 0)
    }
}