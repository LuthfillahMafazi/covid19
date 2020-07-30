package com.zii.covid19

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var adapters : CountryAdapter
    private var descending: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getCountry()

        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapters.filter.filter(newText)
                return true
            }

        })
        sequence.setOnClickListener {
            sequence(descending)
            descending = !descending
        }
    }

    private fun sequence(descending : Boolean) {
        recyclerViewCountry.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            if(descending) {
                (layoutManager as LinearLayoutManager).reverseLayout = true
                (layoutManager as LinearLayoutManager).stackFromEnd = true

            } else {
                (layoutManager as LinearLayoutManager).reverseLayout = false
                (layoutManager as LinearLayoutManager).stackFromEnd = false
            }
            adapter = adapters
        }
    }

    private fun getCountry() {
        val okHttp = OkHttpClient().newBuilder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15,TimeUnit.SECONDS)
            .writeTimeout(15,TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.covid19api.com/")
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)
        api.getAllCountry().enqueue(object : Callback<AllCountry>{
            override fun onFailure(call: Call<AllCountry>, t: Throwable) {
                progress_Bar?.visibility = View.GONE
//                handleError
            }

            override fun onResponse(call: Call<AllCountry>, response: Response<AllCountry>) {
                if(response.isSuccessful) {
                    val getListDataCorona = response.body()?.Global
//                    val formatter: NumberFormat = DecimalFormat("#,####")
                    confirmed_globe.text = getListDataCorona?.TotalConfirmed
                    recovered_globe.text = getListDataCorona?.TotalRecovered
                    deaths_globe.text = getListDataCorona?.TotalDeaths
                    recyclerViewCountry.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(this@MainActivity)
                        progress_Bar?.visibility = View.GONE
                        adapters = CountryAdapter(
                            response.body()!!.Countries as ArrayList<Negara>
                        ) {
                            negara -> itemClicked(negara)
                        }
                        adapter = adapters
                    }

                }
            }

        })
    }
    private fun itemClicked(negara: Negara) {
        val pindahData = Intent(this, ChartCountryActivity::class.java)
        pindahData.putExtra(ChartCountryActivity.Extra_Country, negara.Country)
        pindahData.putExtra(ChartCountryActivity.Extra_LatesUpdate, negara.Date)
        pindahData.putExtra(ChartCountryActivity.Extra_NewDeath, negara.NewDeaths)
        pindahData.putExtra(ChartCountryActivity.Extra_NewConfirmed, negara.NewConfirmed)
        pindahData.putExtra(ChartCountryActivity.Extra_NewRecovered, negara.NewRecovered)
        pindahData.putExtra(ChartCountryActivity.Extra_TotalDeatch, negara.TotalDeaths)
        pindahData.putExtra(ChartCountryActivity.Extra_TotalConfirmed, negara.TotalConfirmed)
        pindahData.putExtra(ChartCountryActivity.Extra_TotalRecovered, negara.TotalRecovered)
        pindahData.putExtra(ChartCountryActivity.Extra_CountryId, negara.CountryCode)
        startActivity(pindahData)
    }
}
