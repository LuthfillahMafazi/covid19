package com.zii.covid19

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.android.synthetic.main.activity_chart_country.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ChartCountryActivity : AppCompatActivity() {

    companion object {
        var Extra_Country = "extra_country"
        var Extra_LatesUpdate = "latest_update"
        var Extra_NewDeath = "extra_newdeath"
        var Extra_NewConfirmed = "extra_newconfirmed"
        var Extra_NewRecovered = "extra_newrecovered"
        var Extra_TotalDeatch = "extra_totaldeath"
        var Extra_TotalConfirmed = "extra_totalconfirmed"
        var Extra_TotalRecovered = "extra_totalrecovered"
        var Extra_CountryId = "extra_countryid"
    }

    private var txtNegara : String? = null
    private var dayCases : ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart_country)

        txtNegara = intent.getStringExtra(Extra_Country)
        val txtLatesUpdate = intent.getStringExtra(Extra_LatesUpdate)
        val txtNewDeath = intent.getStringExtra(Extra_NewDeath)
        val txtNewConfirmed = intent.getStringExtra(Extra_NewConfirmed)
        val txtNewRecovered = intent.getStringExtra(Extra_NewRecovered)
        val txtTotalDeath = intent.getStringExtra(Extra_TotalDeatch)
        val txtTotalConfirmed = intent.getStringExtra(Extra_TotalConfirmed)
        val txtTotalRecovered = intent.getStringExtra(Extra_TotalRecovered)
        val txtCountryId = intent.getStringExtra(Extra_CountryId)

        txt_name_country.text = txtNegara
        latest_update.text = txtLatesUpdate
        hasil_total_confirmed_currently.text = txtTotalConfirmed
        hasil_total_death_currently.text = txtTotalDeath
        hasil_total_recovery_currently.text = txtTotalRecovered
        hasil_new_confirmed_currently.text = txtNewConfirmed
        hasil_new_recovery_currently.text = txtNewRecovered
        hasil_new_death_currently.text = txtNewDeath
        Glide.with(this).load("https://www.countryflags.io/$txtCountryId/shiny/64.png").into(img_flag_country)

        chartDataView()

    }
    private fun chartDataView() {
        val okHttp = OkHttpClient().newBuilder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.covid19api.com/dayone/country/")
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api  = retrofit.create(InfoService::class.java)
        api.getInfoService(txtNegara).enqueue(object : Callback<List<InfoCountry>> {
            override fun onFailure(call: Call<List<InfoCountry>>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(
                call: Call<List<InfoCountry>>, response: Response<List<InfoCountry>>) {
                if(response.isSuccessful) {
                    var getListDataCorona : List<InfoCountry> = response.body()!!

                    val barEntriesConfirmed: ArrayList<BarEntry> = ArrayList()
                    val barEntriesDeath: ArrayList<BarEntry> = ArrayList()
                    val barEntriesRecovered: ArrayList<BarEntry> = ArrayList()
                    val barEntriesActive: ArrayList<BarEntry> = ArrayList()

                    var i = 0
                    while (i < getListDataCorona.size) {
                        for (s in getListDataCorona) {
                            val bEntryConfirmed = BarEntry(i.toFloat(), s.Confirmed.toFloat())
                            val bEntryDeath = BarEntry(i.toFloat(), s.Deaths.toFloat())
                            val bEntryRecovered = BarEntry(i.toFloat(), s.Recovered.toFloat())
                            val bEntryActive = BarEntry(i.toFloat(), s.Active.toFloat())

                            barEntriesConfirmed.add(bEntryConfirmed)
                            barEntriesDeath.add(bEntryDeath)
                            barEntriesRecovered.add(bEntryRecovered)
                            barEntriesActive.add(bEntryActive)

                            dayCases?.add(s.Date)

                            i++
                        }
                    }
                    val barDataSetRecovered = BarDataSet(barEntriesRecovered,"Recovered")
                    val barDataSetDeath = BarDataSet(barEntriesDeath,"Death")
                    val barDataSetConfirmed = BarDataSet(barEntriesConfirmed,"Confirmed")
                    val barDataSetActive = BarDataSet(barEntriesActive,"Active")

                    barDataSetRecovered.setColor(Color.BLUE)
                    barDataSetConfirmed.setColor(Color.GREEN)
                    barDataSetDeath.setColor(Color.RED)
                    barDataSetActive.setColor(Color.BLACK)

                    val x: XAxis = barChartView.xAxis
                    x.valueFormatter = IndexAxisValueFormatter(dayCases)
                    barChartView.axisLeft.axisMinimum = 0f
                    x.position = XAxis.XAxisPosition.BOTTOM
                    x.granularity = 1f
                    x.setCenterAxisLabels(true)
                    x.isGranularityEnabled = true

                    val data = BarData(barDataSetRecovered, barDataSetConfirmed, barDataSetDeath, barDataSetActive)
                    barChartView.data = data

                    val barSpace = 0.02f
                    val groupSpace = 0.3f
                    val groupCount = 4f

                    data.barWidth = 0.15f
                    barChartView.invalidate()
                    barChartView.setNoDataTextColor(R.color.black)
                    barChartView.setTouchEnabled(true)
                    barChartView.description.isEnabled = false
                    barChartView.xAxis.axisMinimum = 0f
                    barChartView.setVisibleXRangeMaximum(0f + barChartView.barData.getGroupWidth(groupSpace,barSpace) * groupCount)
                    barChartView.groupBars(0f,groupSpace, barSpace)
                } else {

                }
            }

        })
    }
}
