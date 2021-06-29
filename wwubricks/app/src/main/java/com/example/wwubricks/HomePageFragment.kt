package com.example.wwubricks

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import java.net.URL
import kotlin.concurrent.thread

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomePageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomePageFragment( ) : Fragment() {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var voteText: String = "upvotes: 0 downvotes 0"
    var picNum: Int = 1
    var url:String ="https://www.hippyclipper.dev/pics/${picNum}.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_home_page, container, false)
        // Inflate the layout for this fragment
        var webView: WebView = view.findViewById(R.id.webview)
        var voteTextView: TextView = view.findViewById(R.id.vote_text)
        voteTextView.text = voteText
        webView.settings.loadWithOverviewMode = true;
        webView.settings.useWideViewPort = true;
        var upButton: ImageButton = view.findViewById(R.id.upvote_button)
        var downButton: ImageButton = view.findViewById(R.id.downvote_button)
        var randomImage:Button = view.findViewById(R.id.newPic)
        webView.loadUrl(url)


        thread{
            var url = URL("http://hippyclipper.dev:8000?action=getVote&postID=${picNum}")

            var text = url.readText()
            val voteList = handleResponse(text)
            activity?.runOnUiThread{
                voteTextView.text = "upvotes: ${voteList[1]}, downvotes: ${voteList[2]}"
            }
        }
        upButton.setOnClickListener{
            thread {
                var urlString = "http://hippyclipper.dev:8000?action=vote&postID=${picNum}&voteType=upvote"
                var url = URL(urlString)
                var retText = url.readText()
                var voteList  = handleResponse(retText)
                activity?.runOnUiThread {
                    Toast.makeText(activity, "upvoted", Toast.LENGTH_LONG).show()
                }

            }
        }
        downButton.setOnClickListener{
            thread {
                var urlString = "http://hippyclipper.dev:8000?action=vote&postID=${picNum}&voteType=downvote"
                var url = URL(urlString)
                var retText = url.readText()
                var voteList  = handleResponse(retText)
                activity?.runOnUiThread {
                    Toast.makeText(activity, "downvoted", Toast.LENGTH_LONG).show()
                }
            }
        }
        randomImage.setOnClickListener{
            picNum = (0..23).random()
            url ="https://www.hippyclipper.dev/pics/${picNum}.jpg"
            webView.loadUrl(url)

            thread{
                var url = URL("http://hippyclipper.dev:8000?action=getVote&postID=${picNum}")

                var text = url.readText()
                val voteList = handleResponse(text)
                activity?.runOnUiThread{
                    voteTextView.text = "upvotes: ${voteList[1]}, downvotes: ${voteList[2]}"
                }
            }

        }

        return view
    }

    private fun handleResponse(votes: String): List<String>{
        return listOf(*votes.split(",").toTypedArray())
    }
    fun url(url:String){
        this.url = url
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomePageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                HomePageFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}