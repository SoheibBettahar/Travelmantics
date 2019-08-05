package com.example.travelmantics

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        setSupportActionBar(toolbar)

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.list_activity_menu, menu)
        val insertItem = menu!!.findItem(R.id.item_insert)
        insertItem.isVisible = FirebaseUtil.isAdmin

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.item_insert -> {
                val intent = Intent(this@ListActivity, DealActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.item_logout -> {
                FirebaseUtil.signOut(this)
                FirebaseUtil.detachListener()
                goToSignInActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun goToSignInActivity() {
        val intent = Intent(this,SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onPause() {
        super.onPause()
        FirebaseUtil.detachListener()
    }

    override fun onResume() {
        super.onResume()
        FirebaseUtil.openFbReference(this)
        FirebaseUtil.attachListener()
        rvDeals.adapter = DealAdapter()
    }

}
