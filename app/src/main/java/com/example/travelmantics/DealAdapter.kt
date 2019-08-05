package com.example.travelmantics

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class DealAdapter : RecyclerView.Adapter<DealAdapter.DealViewHolder>() {


    private var deals: ArrayList<TravelDeal>
    private var mFirebaseDatabase: FirebaseDatabase?
    private var mDatabaseReference: DatabaseReference?
    private var mChildEventListener: ChildEventListener

    init {
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase
        mDatabaseReference = FirebaseUtil.mDatabaseReference
        deals = FirebaseUtil.mDeals!!
        mChildEventListener = object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            @SuppressLint("SetTextI18n")
            override fun onChildAdded(dataSbapshot: DataSnapshot, p1: String?) {
                val travelDeal = dataSbapshot.getValue(TravelDeal::class.java)

                travelDeal?.let {
                    it.id = dataSbapshot.key ?: ""
                    deals.add(it)
                }
                notifyItemInserted(deals.size - 1)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val travelDeal = dataSnapshot.getValue(TravelDeal::class.java)
                deals.remove(travelDeal)
                notifyDataSetChanged()
            }
        }

        mDatabaseReference?.addChildEventListener(mChildEventListener)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DealViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_deal, parent, false)
        return DealViewHolder(view)
    }

    override fun getItemCount(): Int = deals.size

    override fun onBindViewHolder(holder: DealViewHolder, position: Int) {
        holder.bind(deals[position])
    }

    inner class DealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private var tvTitle: TextView? = null
        private var tvDescription: TextView? = null
        var tvPrice: TextView? = null
        var ivPicture: ImageView? = null

        init {
            tvTitle = itemView.findViewById(R.id.tvTitle)
            tvPrice = itemView.findViewById(R.id.tvPrice)
            ivPicture = itemView.findViewById(R.id.imageDeal)
        }

        fun bind(deal: TravelDeal) {
            tvTitle?.text = deal.title
            tvPrice?.text = "${deal.price}$"
            ivPicture?.showImage(deal.imageUrl)
            itemView.setOnClickListener(this)

        }

        private fun ImageView.showImage(url: String?) {
            if (url != null && url.isNotEmpty()) {
                Picasso.get()
                    .load(url)
                    .resize(160, 160)
                    .centerCrop()
                    .placeholder(R.drawable.image)
                    .into(this)
            }
        }

        override fun onClick(view: View) {
            val position = adapterPosition
            val deal = deals[position]
            val intent = Intent(view.context, DealActivity::class.java)
            intent.putExtra("deal", deal)
            view.context.startActivity(intent)
        }
    }


}