package com.example.myfirebaseexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.example.myfirebaseexample.api.FirebaseApiAdapter
import com.example.myfirebaseexample.api.response.ComputerResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    // Referenciar campos de las interfaz
    private lateinit var idSpinner: Spinner
    private lateinit var nameField: EditText
    private lateinit var descriptionField: EditText
    private lateinit var costField: EditText
    private lateinit var brandField: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonLoad: Button

    // Referenciar la API
    private var firebaseApi = FirebaseApiAdapter()

    // Mantener los nombres e IDs de las armas
    private var computerList = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        idSpinner = findViewById(R.id.idSpinner)
        nameField = findViewById(R.id.nameField)
        descriptionField = findViewById(R.id.descriptionField)
        costField = findViewById(R.id.costField)
        brandField = findViewById(R.id.brandField)
        buttonLoad = findViewById(R.id.buttonLoad)
        buttonLoad.setOnClickListener {
            Toast.makeText(this, "Cargando información", Toast.LENGTH_SHORT).show()
            runBlocking {
                getComputerFromApi()
            }
        }

        buttonSave = findViewById(R.id.buttonSave)
        buttonSave.setOnClickListener {
            Toast.makeText(this, "Guardando información", Toast.LENGTH_SHORT).show()
            runBlocking {
                sendComputerToApi()
            }
        }

        runBlocking {
            populateIdSpinner()
        }
    }

    private suspend fun populateIdSpinner() {
        val response = GlobalScope.async(Dispatchers.IO) {
            firebaseApi.getComputers()
        }
        val computers = response.await()
        computers?.forEach { entry ->
            computerList.add("${entry.key}: ${entry.value.name}")
        }
        val computerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, computerList)
        with(idSpinner) {
            adapter = computerAdapter
            setSelection(0, false)
            gravity = Gravity.CENTER
        }
    }

    private suspend fun getComputerFromApi() {
        val selectedItem = idSpinner.selectedItem.toString()
        val computerId = selectedItem.subSequence(0, selectedItem.indexOf(":")).toString()
        println("Loading ${computerId}... ")
        val computerResponse = GlobalScope.async(Dispatchers.IO) {
            firebaseApi.getComputer(computerId)
        }
        val computer = computerResponse.await()
        nameField.setText(computer?.name)
        brandField.setText(computer?.brand)
        descriptionField.setText(computer?.description)
        costField.setText("${computer?.cost}")
    }

    private suspend fun sendComputerToApi() {
        val computerName = nameField.text.toString()
        val brandName = brandField.text.toString()
        val description = descriptionField.text.toString()
        val cost = costField.text.toString().toLong()
        val computer = ComputerResponse("", computerName, description, cost, brandName)
        val ComputerResponse = GlobalScope.async(Dispatchers.IO) {
            firebaseApi.setComputer(computer)
        }
        val response = ComputerResponse.await()
        nameField.setText(computer?.name)
        brandField.setText(computer?.brand)
        descriptionField.setText(computer?.description)
        costField.setText("${computer?.cost}")

        computerList= arrayListOf<String>()
        populateIdSpinner()
    }
}
