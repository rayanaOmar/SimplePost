package com.example.simplepost

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    lateinit var userEnter: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        userEnter = findViewById(R.id.editText)
        val recyclerView = findViewById<RecyclerView>(R.id.rv) as RecyclerView

        val names = ArrayList<People>()

        recyclerView.adapter= RVadapter(this, names)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)
        val progressDialog = ProgressDialog(this@MainActivity)
        progressDialog.setMessage("Please wait")
        progressDialog.show()
        if (apiInterface != null) {
            apiInterface.getname()?.enqueue(object : Callback<ArrayList<People>> {
                override fun onResponse(
                    call: Call<ArrayList<People>>,
                    response: Response<ArrayList<People>>
                ) {
                    progressDialog.dismiss()
                    for(name in response.body()!!){
                        names.add(name)
                    }
                    recyclerView.adapter?.notifyDataSetChanged()
                }
                override fun onFailure(call: Call<ArrayList<People>>, t: Throwable) {
                    //  onResult(null)
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, ""+t.message, Toast.LENGTH_SHORT).show();
                }
            })
        }

    }

    fun addnew(view: android.view.View){
        var f = People(userEnter.text.toString())

        addSingleuser(f, onResult = {
            userEnter.setText("")

            Toast.makeText(applicationContext, "Save Success!", Toast.LENGTH_SHORT).show();
        })
    }
    fun addSingleuser(f: People, onResult: () -> Unit) {

        val apiInterface = APIClient().getClient()?.create(APIInterface::class.java)
        val progressDialog = ProgressDialog(this@MainActivity)
        progressDialog.setMessage("Please wait")
        progressDialog.show()

        if (apiInterface != null) {
            apiInterface.addUser(f).enqueue(object : Callback<ArrayList<People>> {
                override fun onResponse(
                    call: Call<ArrayList<People>>,
                    response: Response<ArrayList<People>>
                ) {

                    onResult()
                    progressDialog.dismiss()
                }

                override fun onFailure(call: Call<ArrayList<People>>, t: Throwable) {
                    onResult()
                    Toast.makeText(applicationContext, "Error!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss()

                }
            })
        }
    }
}
