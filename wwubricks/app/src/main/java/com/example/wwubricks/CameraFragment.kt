package com.example.wwubricks

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CameraFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CameraFragment(var bitmap: Bitmap?) : Fragment() {
    constructor() : this(null) {

    }
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var url:String ="https://scontent-sea1-1.cdninstagram.com/v/t51.2885-15/e35/179799382_126733229491545_1169614857449499497_n.jpg?tp=1&_nc_ht=scontent-sea1-1.cdninstagram.com&_nc_cat=109&_nc_ohc=KhUnxsx7x5kAX-BV5rl&edm=AGenrX8BAAAA&ccb=7-4&oh=e18344780e3eb676009283352f51ca9f&oe=60B696FF&_nc_sid=5eceaa/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_camera, container, false)
        var iv_image: ImageView = view.findViewById(R.id.iv_image)
        iv_image.setImageBitmap(bitmap)
        var text:TextView = view.findViewById(R.id.text3)
        text.text = "Example Post"
        text.textSize=30F
        return view
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