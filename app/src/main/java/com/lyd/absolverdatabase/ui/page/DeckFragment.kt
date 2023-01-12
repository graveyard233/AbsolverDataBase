package com.lyd.absolverdatabase.ui.page

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.state.DeckViewModel
import com.lyd.absolverdatabase.databinding.FragmentDeckBinding
import com.lyd.absolverdatabase.ui.base.BaseFragment

class DeckFragment :BaseFragment() {

    companion object{
        private const val TAG :String = "DeckFragment"
    }

    private var deckBinding : FragmentDeckBinding? = null
    private var deckViewModel : DeckViewModel?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        deckViewModel = getFragmentViewModelProvider(this)[DeckViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view : View = inflater.inflate(R.layout.fragment_deck,container,false)

        deckBinding = FragmentDeckBinding.bind(view)
        deckBinding?.vm = deckViewModel
        deckBinding?.click = ClickProxy()
        deckBinding?.lifecycleOwner = viewLifecycleOwner

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 在这里进行liveData的监听
    }

    inner class ClickProxy {
        fun doMyJob(){
            Log.i(TAG, "doMyJob: ")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // dataBinding要释放掉
        deckBinding = null
    }
}