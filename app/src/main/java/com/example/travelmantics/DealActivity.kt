package com.example.travelmantics

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_deal.*

class DealActivity : AppCompatActivity() {


    private var mFirebaseDatabase: FirebaseDatabase? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var deal: TravelDeal? = null
    private val PICTURE_RESULT = 42
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deal)
        setSupportActionBar(toolbar)
        supportActionBar!!.title =""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        FirebaseUtil.openFbReference(this)
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase
        mDatabaseReference = FirebaseUtil.mDatabaseReference

        val intent = intent
        val travelDeal = intent.getSerializableExtra("deal") as TravelDeal?

        if (travelDeal == null) {
            deal = TravelDeal()
        } else {
            deal = travelDeal
            txtTitle.setText(deal!!.title)
            txtDescription.setText(deal!!.description)
            txtPrice.setText(deal!!.price)
        }
        showImage(deal!!.imageUrl)

    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_menu, menu)
        if (FirebaseUtil.isAdmin) {
            menu!!.findItem(R.id.item_delete).isVisible = true
            menu.findItem(R.id.item_save).isVisible = true
            menu!!.findItem(R.id.item_image).isVisible = true
            setEditTextState(true)
        } else {
            menu!!.findItem(R.id.item_delete).isVisible = false
            menu.findItem(R.id.item_save).isVisible = false
            menu!!.findItem(R.id.item_image).isVisible = false
            setEditTextState(false)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_save -> {
                saveDeal()
                Toast.makeText(this, "Deal saved", Toast.LENGTH_SHORT).show()
                clean()
                finish()
                true
            }

            R.id.item_delete -> {
                deleteDeal()
                Toast.makeText(this, "Deal deleted", Toast.LENGTH_SHORT).show()
                finish()
                true
            }
            R.id.item_image -> {
                val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "image/jpeg"
                    putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                }
                startActivityForResult(Intent.createChooser(intent, "Insert picture"), PICTURE_RESULT)
              true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val imageUri = data?.data

        if (requestCode == PICTURE_RESULT && resultCode == Activity.RESULT_OK && imageUri != null) {
            val imageReference = FirebaseUtil.mStorageReference!!.child(imageUri.lastPathSegment!!)

            imageReference.putFile(imageUri).addOnSuccessListener { taskSnapshot ->
                Toast.makeText(this, "Success : ${imageUri.lastPathSegment}",Toast.LENGTH_SHORT).show()
                Log.d("DealActivity", "Success : ${imageUri.lastPathSegment}")
                imageReference.downloadUrl.addOnSuccessListener {
                    deal!!.imageUrl = it.toString()
                    deal!!.imageName =  taskSnapshot.storage.path
                    showImage(it.toString())
                }

            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Failure: ${exception.toString()}",Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun clean() {
        txtTitle.setText("")
        txtPrice.setText("")
        txtDescription.setText("")
        txtTitle.requestFocus()
    }

    private fun saveDeal() {
        deal!!.title = txtTitle.text.toString()
        deal!!.price = txtPrice.text.toString()
        deal!!.description = txtDescription.text.toString()

        if (deal!!.id == "")
            mDatabaseReference!!.push().setValue(deal)
        else {
            mDatabaseReference!!.child(deal!!.id).setValue(deal)
        }
    }

    private fun deleteDeal() {
        if (deal == null) {
            Toast.makeText(this, "Please insert deal before deleting!", Toast.LENGTH_SHORT).show()
            return
        }
        mDatabaseReference!!.child(deal!!.id).removeValue()
        if(deal!!.imageName.isNotEmpty()){
            //create a new reference
            val pictureReference = FirebaseStorage.getInstance().reference.child(deal!!.imageName)
            pictureReference.delete().addOnSuccessListener {
             Log.d("DealActivity","Image successfully deleted")
            }.addOnFailureListener {
                Log.d("DealActivity","Failed to delete image")
            }

        }
    }

    private fun setEditTextState(isEnabled: Boolean) {
        txtTitle.isEnabled = isEnabled
        txtPrice.isEnabled = isEnabled
        txtDescription.isEnabled = isEnabled
    }

    private fun showImage(url: String?) {
        if (url != null && url.isNotEmpty()) {
            val width = Resources.getSystem().displayMetrics.widthPixels
            Picasso.get()
                .load(url)
                .resize(width, width / 3)
                .centerCrop()
                .into(image)
        }
    }

}
