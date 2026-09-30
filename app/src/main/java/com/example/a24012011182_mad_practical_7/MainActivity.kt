package com.example.a24012011182_mad_practical_7

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException

class MainActivity : AppCompatActivity() {
    val personList = ArrayList<Person>()

    lateinit var db: DatabaseHelper
    lateinit var personsRecycleAdapter: PersonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = DatabaseHelper(this)
        personsRecycleAdapter = PersonAdapter(this, personList)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView1)
        recyclerView?.adapter = personsRecycleAdapter

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab?.setOnClickListener {
            networkDb()
        }

        loadFromDb()
        networkDb()
    }

    private fun loadFromDb() {
        CoroutineScope(Dispatchers.IO).launch {
            val list = db.allPersons
            withContext(Dispatchers.Main) {
                if (list.isNotEmpty()) {
                    personList.clear()
                    personList.addAll(list)
                    personsRecycleAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    val TAG = "MainActivity"

    fun getpersonnData(data: String) {
        try {
            val jsonArray = JSONArray(data)
            personList.clear()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val person = Person(jsonObject)
                personList.add(person)
                try {
                    if (db.getPerson(person.id) != null) {
                        db.updatePerson(person)
                    } else {
                        db.insertPerson(person)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error inserting/updating person in DB: ${e.message}", e)
                }
            }
            personsRecycleAdapter.notifyDataSetChanged()
        } catch (e: JSONException) {
            Log.e(TAG, "getpersonnData JSONException: ${e.message}", e)
        }
    }

    fun deletePer(position: Int) {
        if (position in 0 until personList.size) {
            val person = personList[position]
            db.deletePerson(person)
            personList.removeAt(position)
            personsRecycleAdapter.notifyItemRemoved(position)
            personsRecycleAdapter.notifyItemRangeChanged(position, personList.size - position)
        }
    }

    fun networkDb() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val data = HttpRequest().makeServiceCall(
                    "https://api.json-generator.com/templates/5rDXHcbgpo93/data",
                    "d7wrtfqywyhu7y2bcbsz3cgjpbfisuhnmbibvgvf"
                )
                Log.d(TAG, "networkDb response: $data")
                withContext(Dispatchers.Main) {
                    if (!data.isNullOrEmpty()) {
                        getpersonnData(data)
                    } else {
                        Log.e(TAG, "networkDb: received null or empty response from API")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "networkDb error: ${e.message}", e)
            }
        }
    }

    fun cleanString(activity: Activity, key: String, value: String) {
        val sharepr = activity.getSharedPreferences("app_setting_data", Context.MODE_PRIVATE)
        val editer = sharepr.edit()
        editer.clear()
        editer.commit()
    }

    fun getstring(activity: Activity, key: String): String? {
        val shardepr = activity.getSharedPreferences("app_setting_data", Context.MODE_PRIVATE)
        return shardepr.getString(key, "")
    }

    fun storeString(activity: Activity, key: String, value: String) {
        val sharepr = activity.getSharedPreferences("app_setting_data", Context.MODE_PRIVATE)
        val editer = sharepr.edit()
        editer.putString(key, value)
        editer.commit()
    }
}