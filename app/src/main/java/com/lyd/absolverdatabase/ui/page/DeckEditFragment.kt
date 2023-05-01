package com.lyd.absolverdatabase.ui.page

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.lyd.absolverdatabase.App
import com.lyd.absolverdatabase.R
import com.lyd.absolverdatabase.bridge.data.bean.Deck
import com.lyd.absolverdatabase.bridge.state.DeckState
import com.lyd.absolverdatabase.bridge.state.DeckViewModelFactory
import com.lyd.absolverdatabase.databinding.FragmentDeckEditBinding
import com.lyd.absolverdatabase.ui.base.BaseFragment

class DeckEditFragment :BaseFragment() {

    private val viewModel : DeckState by navGraphViewModels(navGraphId = R.id.nav_deck, factoryProducer = {
        DeckViewModelFactory((mActivity?.application as App).deckRepository)
    })

    private val args :DeckEditFragmentArgs by navArgs()

    private var dataBinding :FragmentDeckEditBinding ?= null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view : View = inflater.inflate(R.layout.fragment_deck_edit,container,false)

        dataBinding = FragmentDeckEditBinding.bind(view)
        dataBinding?.lifecycleOwner = viewLifecycleOwner

        Log.i(TAG, "onCreateView: ${args.deckForEdit}")

        dataBinding?.apply {
            fragmentDeckEditText1.text = args.deckForEdit.toString()
            fragmentDeckEditText1.setOnClickListener {
                nav().popBackStack()
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }


    override fun onDestroyView() {
        super.onDestroyView()
        // dataBinding要释放掉
        dataBinding = null
    }
}