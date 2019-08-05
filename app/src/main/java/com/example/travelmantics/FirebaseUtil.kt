package com.example.travelmantics

import android.app.Activity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirebaseUtil private constructor() {

    companion object {
        private var mFirebaseUtil: FirebaseUtil? = null

        var mFirebaseDatabase: FirebaseDatabase? = null
        var mDatabaseReference: DatabaseReference? = null

        var mFirebaseAuth: FirebaseAuth? = null
        var mAuthListener: FirebaseAuth.AuthStateListener? = null

        var mStorage: FirebaseStorage? = null
        var mStorageReference: StorageReference? = null

        var mDeals: ArrayList<TravelDeal>? = null
        var isAdmin: Boolean = false

        fun openFbReference(activity: Activity) {

            if (mFirebaseUtil == null) {
                mFirebaseUtil = FirebaseUtil()
                mFirebaseDatabase = FirebaseDatabase.getInstance()
                mFirebaseAuth = FirebaseAuth.getInstance()

            }
            mDeals = ArrayList()
            mDatabaseReference = mFirebaseDatabase!!.reference.child("travelDeals")
            mAuthListener = FirebaseAuth.AuthStateListener {
                if (it.currentUser != null) {
                    val userId = it.currentUser!!.uid
                    checkAdmin(userId, activity)
                }
                Toast.makeText(activity.baseContext, "Welcome Back!", Toast.LENGTH_LONG).show()
            }
            connectStorage()
        }

        private fun connectStorage() {
            mStorage = FirebaseStorage.getInstance()
            mStorageReference = mStorage!!.reference.child("deals_images")
        }

        private fun checkAdmin(userId: String, activity: Activity) {

            val adminReference = mFirebaseDatabase!!.reference.child("administrators").child(userId)

            val listener = object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    isAdmin = false
                    showMenu(activity)
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                }
            }
            adminReference.addChildEventListener(listener)
        }

        private fun showMenu(activity: Activity) {
            activity.invalidateOptionsMenu()
        }


        fun signOut(activity: Activity) {
            mFirebaseAuth!!.signOut()
        }

        fun attachListener() {
            mFirebaseAuth!!.addAuthStateListener(mAuthListener!!)
        }

        fun detachListener() {
            mFirebaseAuth!!.removeAuthStateListener(mAuthListener!!)
        }
    }


}