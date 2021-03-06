package org.calinrc.smarthouseremote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import org.calinrc.smarthouseremote.data.ServerCredentials
import org.calinrc.smarthouseremote.data.HomeResponse
import org.calinrc.smarthouseremote.data.ParsersFactory
import org.calinrc.smarthouseremote.data.model.HomeServerModel
import org.calinrc.smarthouseremote.data.model.HomeServerModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var homeServerModel: HomeServerModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        val serverUrl = sharedPreferences.getString("server_url", "")
        val username = sharedPreferences.getString("username", "")
        val password = sharedPreferences.getString("password", "")

        homeServerModel = ViewModelProvider(
            this,
            HomeServerModelFactory(
                ServerCredentials(
                    serverUrl ?: "",
                    username ?: "",
                    password ?: ""
                ), ParsersFactory()
            )
        ).get(HomeServerModel::class.java)

        val statusImage: ImageView = findViewById(R.id.imageView)
        val textField: TextView = findViewById(R.id.textViewDetails)

        homeServerModel.statusResult.observe(this@MainActivity, Observer {
            val result = it ?: return@Observer
            when (result) {
                is HomeResponse.HomeStatusResponse -> {
                    statusImage.setImageResource(android.R.drawable.presence_online)
                    textField.text=""
                    //textField.text = result.response
                    //Toast.makeText(this, result.response, Toast.LENGTH_LONG).show()
                }
                is HomeResponse.FailedHomeResponse -> {
                    textField.text = result.exception.message
                    if (result.statusCode/100 == 4){
                        homeServerModel.suspend()
                        statusImage.setImageResource(android.R.drawable.presence_busy)
                        //Toast.makeText(this, result.statusCode.toString(), Toast.LENGTH_LONG).show()
                    }else {
                        statusImage.setImageResource(android.R.drawable.presence_offline)
                        //Toast.makeText(this, result.statusCode.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
        val gateButton: Button = findViewById(R.id.button_gate)
        val doorButton: Button = findViewById(R.id.button_door)
        gateButton.setOnClickListener {
            openGate()
        }
        doorButton.setOnClickListener {
            openDoor()
        }


    }

    override fun onResume() {
        homeServerModel.getStatus()
        super.onResume()
    }

    override fun onPause() {
        homeServerModel.suspend()
        super.onPause()
    }

    private fun openGate() {
        homeServerModel.gateStateChange()
    }

    private fun openDoor() {
        homeServerModel.doorStateChange()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                //findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.action_FirstFragment_to_SecondFragment)
                //findNavController(R.id.nav_host_fragment_content_main)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}