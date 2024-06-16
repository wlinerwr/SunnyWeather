package com.sunnyweather.android.ui.place

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunnyweather.android.databinding.FragmentPlaceBinding

class PlaceFragment : Fragment() {

    val viewModel by lazy { ViewModelProvider(this).get(PlaceViewModel::class.java) }

    private lateinit var adapter: PlaceAdapter

    lateinit var binding: FragmentPlaceBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val lifecycleObserver = object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                val layoutManager = LinearLayoutManager(activity)
                binding.recyclerView.layoutManager = layoutManager
                adapter = PlaceAdapter(this@PlaceFragment, viewModel.placeList)
                binding.recyclerView.adapter = adapter
                binding.searchPlaceEdit.addTextChangedListener { editable ->
                    Log.d("Main", "onCreate: dsadasdasd")
                    val context = editable.toString()
                    if (context.isNotEmpty()) {
                        viewModel.searchPlaces(context)
                    } else {
                        binding.recyclerView.visibility = View.GONE
                        binding.bgImageView.visibility = View.VISIBLE
                        viewModel.placeList.clear()
                        adapter.notifyDataSetChanged()
                    }
                }
                viewModel.placeLiveData.observe(owner, Observer { result ->
                    val place = result.getOrNull()
                    if (place != null) {
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.bgImageView.visibility = View.GONE
                        viewModel.placeList.clear()
                        viewModel.placeList.addAll(place)
                    } else {
                        Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                        result.exceptionOrNull()?.printStackTrace()
                    }
                })
                owner.lifecycle.removeObserver(this)
            }
        }
        activity?.lifecycle?.addObserver(lifecycleObserver)

    }
}

