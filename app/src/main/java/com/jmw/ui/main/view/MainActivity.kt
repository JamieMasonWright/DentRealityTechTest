package com.jmw.ui.main.view

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.jmw.R
import com.jmw.data.api.ApiHelper
import com.jmw.data.api.ApiServiceImpl
import com.jmw.data.model.Country
import com.jmw.ui.base.ViewModelFactory
import com.jmw.ui.main.adapter.MainAdapter
import com.jmw.ui.main.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.lang.Math.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter: MainAdapter
    var homeMarker: Marker? = null
    var listOfTouringCountries = ArrayList<CharSequence>()
    var listOfDistances = ArrayList<Double>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("Main Activity", "")
        setupUI()
        setupViewModel()
        setupObserver()
    }

    private fun setupUI() {
        fab.setOnClickListener {
            // setup the alert builder

            if (homeMarker == null) {

                Toast.makeText(this, "Please select your home location", Toast.LENGTH_LONG).show()

            } else {
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setTitle(getString(R.string.please_select_up_to))
                // add a checkbox list
                val listOfCountriesName = ArrayList<CharSequence>()
                val listOfCountriesSelected = ArrayList<Boolean>()
                val listOfCountriesSelectedLat = ArrayList<Double>()
                val listOfCountriesSelectedLng = ArrayList<Double>()

                for (item in mainViewModel.getCountries()!!) {
                    // body of loop
                    val list = homeMarker!!.title?.split('|')

                    // if (list?.get(0)?.equals(item.name) == false) {
                    listOfCountriesName.add(item.name)
                    listOfCountriesSelected.add(false)
                    listOfCountriesSelectedLat.add(item.latlng?.get(0)?.toDouble()!!)
                    listOfCountriesSelectedLng.add(item.latlng[1].toDouble())
                    //  }

                }

                val countries = listOfCountriesName.toTypedArray()
                val checkedItems = listOfCountriesSelected.toBooleanArray()

                builder.setMultiChoiceItems(countries, checkedItems) { dialog, which, isChecked ->

                }

                // add OK and Cancel buttons
                builder.setPositiveButton("Calculate shortest distance") { dialog, which ->
                    // user clicked OK

                    var x = 0
                    var neededIndex: Int = 0

                    var str = ""

                    val list = homeMarker!!.title?.split('|')

                    //
                    for ((y, item) in checkedItems.withIndex()) {

                        if (list?.get(0)?.equals(countries[y]) == true) {
                            str += num2char(y)
                            neededIndex = y
                        }

                        if (item) {
                            x += 1
                            str += num2char(y)
                        }

                    }



                    if (x > 1) {

                        listOfTouringCountries = ArrayList()
                        val n = str.length
                        permute(str, 0, n - 1, num2char(neededIndex))
                        listOfDistances = ArrayList()

                        for (item in listOfTouringCountries) {

                            var xxx = 0.0

                            for (i in 0..item.length - 2) {

                                xxx += calculateDistance(
                                    listOfCountriesSelectedLat[char2num(
                                        "" + item[i]
                                    )],
                                    listOfCountriesSelectedLng[char2num("" + item[i])],
                                    listOfCountriesSelectedLat[char2num("" + item[i + 1])],
                                    listOfCountriesSelectedLng[char2num("" + item[i + 1])]
                                )

                            }

                            listOfDistances.add(xxx)

                            Log.d(
                                "Permutation ",
                                "" + item + " | " + xxx + " | " + listOfDistances.size
                            )
                        }

                        val smallestElement = findMin(listOfDistances)

                        Log.d("Permutation ", "smallest $smallestElement")

                        var y: Int = -1
                        for (item in listOfTouringCountries) {
                            y++
                            if (smallestElement == listOfDistances.get(y)) {
                                shortestJourney(listOfTouringCountries.get(y))
                                break;
                            }
                        }

                    } else {
                        Toast.makeText(this, "Please Select 4 Countries", Toast.LENGTH_LONG).show()
                    }

                }
                builder.setNegativeButton("Cancel", null)

                // create and show the alert dialog
                val dialog = builder.create()
                dialog.show()
            }
        }

        with(mapView) {
            // Initialise the MapView
            onCreate(null)
            // Set the map ready callback to receive the GoogleMap object
            getMapAsync {
                MapsInitializer.initialize(applicationContext)
                setMapLocation(it)
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MainAdapter(arrayListOf())
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        recyclerView.adapter = adapter
    }

    private fun shortestJourney(get: CharSequence) {
        var nameOfCountries = ""
        for (i in get) {
            nameOfCountries =
                nameOfCountries + mainViewModel.getCountries()!![char2num("" + i)].name + "\n"
        }
        AlertDialog.Builder(this@MainActivity)
            .setTitle("Country Details")
            .setMessage(nameOfCountries)
            .setNegativeButton("OK", null)
            .show()
    }

    private fun setupObserver() {
        if (mainViewModel.getCountries() != null) {
            progressBar.visibility = View.GONE
            renderList(mainViewModel.getCountries()!!)
        } else {
            Toast.makeText(this, "Error Fetching List", Toast.LENGTH_LONG).show()
        }
    }

    fun findMin(list: ArrayList<Double>): Double {
        var min = Double.MAX_VALUE
        for (i in list) {
            if (i < min) {
                min = i
                Log.d("Smallest ::: ", "" + min)
            }
        }
        return min
    }

    private fun renderList(countries: List<Country>) {
        adapter.addData(countries)
        adapter.notifyDataSetChanged()
    }

    private fun setupViewModel() {


        var jsonString: String

        try {

            jsonString =
                application.assets.open("countries.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            Log.d("HERE ::", ioException.toString())
            ioException.printStackTrace()
            jsonString = "";
        }

        mainViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(ApiServiceImpl()), jsonString)
        ).get(MainViewModel::class.java)
    }

    private fun setMapLocation(map: GoogleMap) {
        with(map) {

            for (item in mainViewModel.getCountries()!!) {
                // body of loop
                val position =
                    LatLng(item.latlng?.get(0)?.toDouble()!!, item.latlng[1].toDouble())


                addMarker(MarkerOptions().position(position).title(item.name + "|" + item.capital))
                moveCamera(CameraUpdateFactory.newLatLngZoom(position, 3f))
            }

            mapType = GoogleMap.MAP_TYPE_NORMAL

            setOnMarkerClickListener { marker ->

                val list = marker.title?.split('|')

                var details: String = "Country Name : " + (list?.get(0) ?: "Unknown") + "\n" +
                        "Country Capital : " + (list?.get(1) ?: "Unknown") + "\n"

                if (homeMarker != null) {

                    details = "Country Name : " + (list?.get(0) ?: "Unknown") + "\n" +
                            "Country Capital : " + (list?.get(1) ?: "Unknown") + "\n" +
                            "Distance From Home : " + (calculateDistance(
                        marker.position.latitude,
                        marker.position.longitude,
                        homeMarker!!.position.latitude,
                        homeMarker!!.position.longitude
                    )) + " km\n"
                }



                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Country Details")
                    .setMessage(details)
                    .setPositiveButton("Mark Home") { dialogInterface, which ->

                        homeMarker = marker
                        map.clear()
                        for (item in mainViewModel.getCountries()!!) {
                            // body of loop
                            val position = LatLng(
                                item.latlng?.get(0)?.toDouble()!!,
                                item.latlng[1].toDouble()
                            )

                            if (position == homeMarker!!.position) {
                                addMarker(
                                    MarkerOptions().position(position)
                                        .title(item.name + "|" + item.capital)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.house))
                                )
                            } else {
                                addMarker(
                                    MarkerOptions().position(position)
                                        .title(item.name + "|" + item.capital)
                                )
                            }
                        }
                    }
                    .setNegativeButton("OK", null)
                    .show()

                true
            }
        }
    }

    private fun calculateDistance(a1: Double, a2: Double, b1: Double, b2: Double): Int {
        val locationA = Location("")
        locationA.latitude = a1
        locationA.longitude = a2

        val locationB = Location("")
        locationB.latitude = b1
        locationB.longitude = b2

        return (locationA.distanceTo(locationB) / 1000).toInt()
    }


    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


    private fun char2num(a: String): Int {
        var x = -1
        val chars = arrayOf(
            "a",
            "b",
            "c",
            "d",
            "e",
            "f",
            "g",
            "h",
            "i",
            "j",
            "k",
            "l",
            "m",
            "n",
            "o",
            "p",
            "q"
        )
        for (y in chars) {
            x++
            if (a == y) {
                break
            }
        }
        return x
    }

    private fun num2char(a: Int): String {
        val chars = arrayOf(
            "a",
            "b",
            "c",
            "d",
            "e",
            "f",
            "g",
            "h",
            "i",
            "j",
            "k",
            "l",
            "m",
            "n",
            "o",
            "p",
            "q"
        )
        return chars[a]
    }

    private fun permute(str: String, l: Int, r: Int, neededIndex: String) {
        var str = str
        if (l == r) {
            if (str[0] == neededIndex[0]) {
                listOfTouringCountries.add(str)
            }
        } else {
            for (i in l..r) {
                str = swap(str, l, i)
                permute(str, l + 1, r, neededIndex)
                str = swap(str, l, i)
            }
        }

    }

    /**
     * Swap Characters at position
     * @param a string value
     * @param i position 1
     * @param j position 2
     * @return swapped string
     */
    private fun swap(a: String, i: Int, j: Int): String {
        val temp: Char
        val charArray = a.toCharArray()
        temp = charArray[i]
        charArray[i] = charArray[j]
        charArray[j] = temp
        return String(charArray)
    }
}
