package com.safaa.weatherapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.safaa.weatherapp.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    var API = "9a6e150a64904ba8c5eb31d55f0f3de4"
    var zipCode = ""
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get data from sharedPreferences
        zipCode = getCode()
        Log.d("Main","safaaaaaaaaaaaaaaaaaaa $zipCode")
        requestAPI()

        binding.zipCodeBtn.setOnClickListener {
            val intent = Intent(this, CodeActivity::class.java)
            startActivity(intent)
        }

    }

    private fun requestAPI() {
        CoroutineScope(IO).launch {
            var data = async { fetchData() }.await()
            if(data.isNotEmpty())
                wheatherInfo(data)
            else
                Log.d("MAIN", "unable to get data")
        }
    }

    private fun fetchData():String {
        var response = ""
        try{
            response = URL("https://api.openweathermap.org/data/2.5/weather?zip=$zipCode,&appid=$API").readText()
        }catch (e:Exception){
            Log.d("MAIN","ISSUE: $e")
            //Toast.makeText(this, "Sorry, The city not found !_!", Toast.LENGTH_LONG).show()
            zipCode = "10026"
            requestAPI()
        }
        return response
    }

    private suspend fun wheatherInfo(result:String) {
        withContext(Main){
            val jsonObject = JSONObject(result)

            var statusDisc = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description")

            var main = jsonObject.getJSONObject("main")
            var temp = convertToC(main.getDouble("temp"))
            var min_temp = convertToC(main.getDouble("temp_min"))
            var max_temp = convertToC(main.getDouble("temp_max"))
            var pressure = main.getString("pressure")
            var humidity = main.getString("humidity")

            var wind = jsonObject.getJSONObject("wind").getString("speed")

            var sys = jsonObject.getJSONObject("sys")
            var countryName = sys.getString("country")
            var sunrise = sys.getLong("sunrise")
            var sunset = sys.getLong("sunset")

            var cityName = jsonObject.getString("name")

            var updatedAt = jsonObject.getLong("dt")
            var updatedAtTime = "Uptadted at: "+ SimpleDateFormat("dd/MM/yyyy hh:mm a",Locale.ENGLISH ).format(Date(updatedAt*1000))

            //
            binding.statusTV.text = statusDisc
            binding.temprTV.text = temp + "°C"
            binding.tempMinTv.text = min_temp + "°C"
            binding.tempMaxTv.text = max_temp + "°C"
            binding.cityTV.text = "$cityName, $countryName"
            binding.dateTV.text = updatedAtTime
            binding.sunriseTV.text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise*1000))
            binding.sunsetTV.text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset*1000))
            binding.humidityTV.text = humidity
            binding.windtTV.text = wind
            binding.pressureTV.text = pressure


        }
    }

    private fun convertToC(Kelvin: Double): String {
        var cResult =(Kelvin - 273)
        return String.format("%.1f", cResult)
    }

    fun getCode(): String {
        sharedPreferences = this.getSharedPreferences(
        getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        return sharedPreferences.getString("zipCode", "10026").toString()
    }
}
