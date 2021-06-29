package com.example.wwubricks

import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.webkit.WebView
import androidx.recyclerview.widget.RecyclerView
import com.example.wwubricks.dummy.DummyContent.DummyItem


/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyimageRecyclerViewAdapter(
    private val values: List<DummyItem>
) : RecyclerView.Adapter<MyimageRecyclerViewAdapter.ViewHolder>() {
    interface RecyclerViewListener {
        fun calledFromRecycler(position: Int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }
    var listener: RecyclerViewListener? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView = item.id
        holder.contentView.settings.loadWithOverviewMode = true;
        holder.contentView.settings.useWideViewPort = true;
        val num = position % 23 + 1
        holder.contentView.loadUrl("https://www.hippyclipper.dev/pics/${num}.jpg")
       // Log.d("HI", "$position")

       /* holder.contentView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                Log.d("HI", "$position")
                return true
            }
        }*/
//        holder.itemView.setOnClickListener{
//            Log.d("HI", "$position")
//            listener?.calledFromRecycler(position)
//        }

        holder.contentView.setOnTouchListener(OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP -> if (v.hasFocus()) {
                    listener?.calledFromRecycler(position)
                }
            }
            when (event.action) {

            }
            false
        })



    }
    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var idView = "0"
        val contentView: WebView = view.findViewById(R.id.webviewGallary)

        override fun toString(): String {
            return super.toString() + " '" + idView + "'"
        }
    }
}