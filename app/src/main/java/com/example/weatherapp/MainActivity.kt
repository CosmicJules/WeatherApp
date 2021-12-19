package com.example.weatherapp

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.squareup.picasso.Picasso
import org.json.JSONObject
import org.w3c.dom.Text
import java.lang.Exception
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    val API: String = "3a720b3a3e177f56b462b62605099cdf"
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val button:Button = findViewById(R.id.toForecast)

        button.setOnClickListener(){
            val intent = Intent(this,Forecast::class.java)
            startActivity(intent)
        }

        supportActionBar?.hide()
        weatherDetails().execute()


    }

    inner class weatherDetails() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.idloading).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.RelPar).visibility = View.GONE

        }

        override fun doInBackground(vararg p0: String?): String? {
            var response: String?
            try {
                response =
                    URL("https://api.openweathermap.org/data/2.5/weather?id=2655984&units=metric&appid=$API").readText(
                        Charsets.UTF_8
                    )
            } catch (e: Exception) {
                response = null
            }
            return response
        }


        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val wind = jsonObj.getJSONObject("wind")
                val sys = jsonObj.getJSONObject("sys")

                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val temp = main.getString("temp") + "°C"
                val weatherDesc = weather.getString("main")
                val minTemp = main.getString("temp_min") + "°C"
                val maxTemp = main.getString("temp_max") + "°C"

                val sunRise: Long = sys.getLong("sunrise")
                val sunSet: Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed") + "m/s"
                val windDirect = getCardinalDirections(wind.getString("deg"))
                val cityName = jsonObj.getString("name") + ", " + sys.getString("country")
                val weatherIcon = weather.getString("icon")




                findViewById<TextView>(R.id.cityName).text = cityName
                val sdf = SimpleDateFormat("EEEE, MMMM d, yyyy")
                val currentDate = sdf.format(Date())
                findViewById<TextView>(R.id.currDate).text = currentDate
                findViewById<TextView>(R.id.currentTemperature).text = temp
                findViewById<TextView>(R.id.weatherDesc).text = weatherDesc
                findViewById<TextView>(R.id.minTemp).text = minTemp
                findViewById<TextView>(R.id.maxTemp).text = maxTemp
                findViewById<TextView>(R.id.sunriseTime).text =
                    SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunRise * 1000))
                findViewById<TextView>(R.id.sunsetTime).text =
                    SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunSet * 1000))
                findViewById<TextView>(R.id.windSpeed).text = windSpeed
                findViewById<TextView>(R.id.windDirect).text = windDirect


                imageView = findViewById(R.id.imageView)
                Picasso.get().load("http://openweathermap.org/img/w/$weatherIcon.png")
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(imageView)







                findViewById<ProgressBar>(R.id.idloading).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.RelPar).visibility = View.VISIBLE


            } catch (e: Exception) {

                return;
            }
        }


        fun getForecasts(vararg p0: String?): String? {
            var response: String?
            try {
                response =
                    URL("https://api.openweathermap.org/data/2.5/forecast?id=2655984&units=metric&appid=$API").readText(
                        Charsets.UTF_8
                    )
            } catch (e: Exception) {
                response = null
            }
            return response
        }


        fun fillForecasts(result: String?) {
            try {
                val jsonObj = JSONObject(result)
                val list = jsonObj.getJSONArray("list").getJSONObject(0)
                val main = list.getJSONObject("main")
                val wind = list.getJSONObject("wind")

                val currentDate = list.getString("dt_txt")
                val minTemp = main.getString("temp_min") + "°C"
                val maxTemp = main.getString("temp_max") + "°C"

                val windSpeed = wind.getString("speed") + "m/s"
                val windDirect = getCardinalDirections(wind.getString("deg"))

                val minAndmaxTemp = "Max Temp: $maxTemp, Min Temp: $minTemp"
                val windSpeedAndDir = "Wind: $windDirect at $windSpeed"


                findViewById<TextView>(R.id.day2date).text = currentDate
                findViewById<TextView>(R.id.day2temp).text = minAndmaxTemp
                findViewById<TextView>(R.id.day2wind).text = windSpeedAndDir

            } catch (e: Exception) {

                return;
            }
        }


        fun getCardinalDirections(degrees: String): String {
            val degasInt = degrees.toDouble()
            val directions = arrayOf("↑ N", "↗ NE", "→ E", "↘ SE", "↓ S", "↙ SW", "← W", "↖ NW")
            val doubAngle = (Math.round(degasInt / 45)) % 8
            val angle = doubAngle.toInt()
            return directions[angle]
        }

    }
}